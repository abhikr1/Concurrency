import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class ThunderingHerd {
    Map<Integer, Integer> inMemoryCache = new ConcurrentHashMap<>();
    Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();
    Semaphore semaphore = new Semaphore(10);

    public static void main(String args[]){
        ThunderingHerd thunderingHerd = new ThunderingHerd();

        Runnable runnable1 = () -> thunderingHerd.fetchData(1);
        Runnable runnable2 = () -> thunderingHerd.fetchData(2);
        Runnable runnable3 = () -> thunderingHerd.fetchData(1);
        Runnable runnable4 = () -> thunderingHerd.fetchData(2);
        Runnable runnable5 = () -> thunderingHerd.fetchData(1);
        Runnable runnable6 = () -> thunderingHerd.fetchData(2);
        Runnable runnable7 = () -> thunderingHerd.fetchData(2);

            Thread thread1 = new Thread(runnable1, "1");
            Thread thread2 = new Thread(runnable2, "2");
            Thread thread3 = new Thread(runnable3, "1");
            Thread thread4 = new Thread(runnable4, "2");
            Thread thread5 = new Thread(runnable5, "1");
            Thread thread6 = new Thread(runnable6, "2");
            Thread thread7 = new Thread(runnable7, "2");

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
            thread7.start();
    }
    public void fetchData(int k){
//        String k = Thread.currentThread().getName();
        semaphoreMap.putIfAbsent(k, new Semaphore(1));
        Semaphore semaphore1 = semaphoreMap.get(k);
        try {
        semaphore1.acquire();
        if(inMemoryCache.containsKey(k)){
           System.out.println("IN MEMORY VALUE : "  + inMemoryCache.get(k));
           return;
        }
            //search from reddis cache
            if(inMemoryCache.containsKey(k)){
                System.out.println("IN MEMORY VALUE : "  + inMemoryCache.get(k));
                return;
            }
            System.out.println(Thread.currentThread().getName() + " Fetching from Redis...");
            Thread.sleep(1000);
            inMemoryCache.put(k, k + 1000);
            System.out.println("CACHED VALUE"  + inMemoryCache.get(k));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            semaphore1.release();
        }
    }

}
