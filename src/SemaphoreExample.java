import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private final Semaphore semaphore = new Semaphore(2);
    public static void main(String args[]){
        SemaphoreExample semaphoreExample = new SemaphoreExample();
//        Runnable runnable = semaphoreExample::semaphoreImplmentation;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                semaphoreExample.semaphoreImplmentation();
            }
        };
        for(int i = 0; i < 10; i++){
            Thread thread = new Thread(runnable, "Thread " + i);
            thread.start();
        }
    }
    public void semaphoreImplmentation(){
        try{
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " acquired a lock");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println(Thread.currentThread().getName() + " release a lock");
            semaphore.release();
        }
    }
}
