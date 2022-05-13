package edu.lysak.kafkaconsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SampleWorker implements Runnable {

    private static final BlockingQueue<String> REQUEST_QUEUE = new ArrayBlockingQueue<>(100);

    private static final AtomicInteger PENDING_ITEMS = new AtomicInteger();

    private final String workerId;

    public SampleWorker(String workerId) {
        super();
        System.out.println("Creating worker for " + workerId);
        this.workerId = workerId;
    }

    public static void addToQueue(String order) {
        PENDING_ITEMS.incrementAndGet();
        try {
            REQUEST_QUEUE.put(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getPendingCount() {
        return PENDING_ITEMS.get();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String order = REQUEST_QUEUE.take();
                System.out.println("Worker " + workerId + " Processing : " + order);

                //Do all required processing
                Thread.sleep(100);

                //After all processing is done
                PENDING_ITEMS.decrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
