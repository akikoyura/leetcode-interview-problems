package com.edu.concurrency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BoundedBlockingQueueTest {

    @Test
    public void testBasicOperations() throws InterruptedException {
        BoundedBlockingQueue queue = new BoundedBlockingQueue(3);

        assertEquals(0, queue.size());

        queue.enqueue(1);
        assertEquals(1, queue.size());

        queue.enqueue(2);
        assertEquals(2, queue.size());

        assertEquals(1, queue.dequeue());
        assertEquals(1, queue.size());

        assertEquals(2, queue.dequeue());
        assertEquals(0, queue.size());
    }

    @Test
    public void testFullCapacity() throws InterruptedException {
        int capacity = 2;
        BoundedBlockingQueue queue = new BoundedBlockingQueue(capacity);

        queue.enqueue(1);
        queue.enqueue(2);

        // Queue is now full
        assertEquals(capacity, queue.size());
    }

    @Test
    public void testConcurrentProducerConsumer() throws InterruptedException {
        int capacity = 5;
        BoundedBlockingQueue queue = new BoundedBlockingQueue(capacity);
        int numOperations = 1000;

        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger sum = new AtomicInteger(0);
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= numOperations; i++) {
                    queue.enqueue(i);
                    produced.incrementAndGet();
                }
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < numOperations; i++) {
                    int value = queue.dequeue();
                    sum.addAndGet(value);
                    consumed.incrementAndGet();
                }
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        latch.await(10, TimeUnit.SECONDS);

        assertEquals(numOperations, produced.get());
        assertEquals(numOperations, consumed.get());

        // Sum of numbers from 1 to numOperations
        int expectedSum = (numOperations * (numOperations + 1)) / 2;
        assertEquals(expectedSum, sum.get());
    }

    @Test
    public void testMultipleProducersConsumers() throws InterruptedException {
        int capacity = 10;
        BoundedBlockingQueue queue = new BoundedBlockingQueue(capacity);
        int numProducers = 4;
        int numConsumers = 4;
        int numItemsPerProducer = 2;
        int totalItems = numProducers * numItemsPerProducer;

        ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);
        CountDownLatch producersLatch = new CountDownLatch(numProducers);
        CountDownLatch consumersLatch = new CountDownLatch(numConsumers);

        AtomicInteger totalConsumed = new AtomicInteger(0);
        // Start producers
        for (int producer = 0; producer < numProducers; producer++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < numItemsPerProducer; i++) {
                        queue.enqueue(1);
                    }
                    producersLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Start consumers
        for (int customer = 0; customer < numConsumers; customer++) {
            executor.submit(() -> {
                try {
                    while (totalConsumed.get() < totalItems) {
                        queue.dequeue();
                        totalConsumed.incrementAndGet();
                    }
                    consumersLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Wait for completion
        assertTrue(producersLatch.await(10, TimeUnit.SECONDS));
        assertTrue(consumersLatch.await(10, TimeUnit.SECONDS));

        assertEquals(totalItems, totalConsumed.get());
        assertEquals(0, queue.size());

        executor.shutdown();
    }
}