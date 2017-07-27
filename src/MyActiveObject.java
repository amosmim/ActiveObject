import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MyActiveObject {
    public interface Func<Return, Param>{
        Return apply(Param param);
    }

    public class Future<Return>{
        Return v;

        public synchronized void set(Return v) {
            this.v = v;
            notifyAll();
        }
        public Return get(){
            if (v == null){
                synchronized (this){
                    try{
                        wait();
                    } catch (InterruptedException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            return v;
        }
    }

    HashMap<String, Func> commands;
    BlockingDeque<Runnable> dispatchQueue;
    volatile boolean stop;
    Thread activeThread;
    public MyActiveObject() {
        this.stop = false;
        dispatchQueue = new LinkedBlockingDeque<Runnable>();
        commands = new HashMap<>();
        activeThread = new Thread(()->{
            while (!stop){
                try {
                    this.dispatchQueue.take().run();
                } catch (InterruptedException e) {
                    System.out.println("error in Active Thread");
                    e.printStackTrace();
                }
            }
        });
        activeThread.start();
    }

    public <Return,Param> void addFunction(String key , Func<Return,Param> func){
        this.commands.put(key,func);
    }

    public <Return,Param> Future<Return> exec (String key , Param param){
        Future<Return> toReturn = new Future<Return>();
        Func<Return,Param> func  = commands.get(key);
        dispatchQueue.add(()-> {
            toReturn.set(func.apply(param));
        });
        return toReturn;
    }

    public void stop() {
        this.stop = true;
        if (dispatchQueue.isEmpty()) {
            synchronized (this.dispatchQueue) {
                this.dispatchQueue.notifyAll();
            }
        }

    }
}
