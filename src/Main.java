import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author Navaneeth Sen
 */
public class Main
{

    public static void main(String[] args) throws InterruptedException
    {
        testWhenSystemReleaseRef(1);
        testWhenSystemReleaseRef(2);
        testWhenSystemReleaseRef(3);
    }

    public static void testWhenSystemReleaseRef(int type)
    {
        // Create an object
        MyObject obj = new MyObject();

        // Create a reference to obj
        ReferenceQueue rq = new ReferenceQueue();
        Reference ref;
        switch (type)
        {
            case 1:
                ref = new SoftReference(obj);
                break;
            case 2:
                ref = new WeakReference(obj);
                break;
            default:
                ref = new PhantomReference(obj, rq);
        }

        System.out.println("+" + ref.getClass().getName());

        System.out.print(" - Assigns obj to null: ");
        obj = null;
        System.out.println("ref is " + (ref.get() == null ? "null" : "not null"));

        // manually calls a Garbage Collection; expects the reference 
        // survived if old generation is not full
        System.out.print(" - Triggers garbage collection: ");
        System.gc();
        System.out.println("ref is " + (ref.get() == null ? "null" : "not null"));

        // makes the Heap full and check the object is reclaimed
        System.out.print(" - Forces the heap overflow: ");
        try
        {
            Object[] objs = new Object[(int) Runtime.getRuntime().maxMemory()];
        }
        catch (Throwable e)
        {
        }
        System.out.println("ref is " + (ref.get() == null ? "null" : "not null"));
    }
}

class MyObject
{

    public MyObject()
    {
        double[] array = new double[1000000];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = Math.random();
        }
    }

    protected void finalize() throws Throwable
    {
        System.out.println("In finalize method for this object: " + this);
    }
}