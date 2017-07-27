import com.sun.org.apache.xalan.internal.utils.FeatureManager;

public class Main {

    public static void main(String[] args) {
	    MyActiveObject active = new MyActiveObject();
	    active.addFunction("mul2", (Double x)->x*2);
        active.addFunction("print", (Object x)->{
            System.out.println(x.toString());
            return x.toString();
        });
        active.addFunction("sqr", (Double x)->x*x);
        active.addFunction("len", (String x)->x.length());
        System.out.println(active.exec("len",
                                active.exec("print",
                                    active.exec("mul2",
                                        active.exec("sqr",2.0)
                                            .get()).get()).get()).get());

        active.stop();
        return;
    }


}
