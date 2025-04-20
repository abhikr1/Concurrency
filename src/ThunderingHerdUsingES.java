import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ThunderingHerdUsingES {
    Map<Integer, Integer> inMemoryCache = new ConcurrentHashMap<>();
    Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();
    Semaphore semaphore = new Semaphore(10);

    public static void main(String args[]){
        ThunderingHerdUsingES thunderingHerd = new ThunderingHerdUsingES();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int[] keys = {1, 2, 1, 2, 1, 2, 2};

        for (int key : keys) {
            executor.submit(() -> thunderingHerd.fetchData(key));
        }

        executor.shutdown();
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

