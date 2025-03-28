package com.edu.concurrency;

/*
Leet Code 1188: Design Bounded Blocking Queue
Problem Requirements
Implement a thread-safe bounded blocking queue that has the following properties:
The queue can have a maximum capacity that is passed to the constructor.
Threads calling enqueue will block when the queue is full until space becomes available.
Threads calling dequeue will block when the queue is empty until an element is available.
Multiple producer and consumer threads should be able to use the queue simultaneously
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue {
    private final Queue<Integer> queue;
    private final int capacity;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    // Constructor initializes the queue with a maximum capacity
    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    // Adds an element to the front of the queue
    // If the queue is full, blocks until space becomes available
    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            // Wait while the queue is full
            while (queue.size() == capacity) {
                notFull.await();
            }
            // Add the element to the queue
            queue.offer(element);
            // Signal that the queue is not empty anymore
            notEmpty.signal();
        } finally {
            lock.unlock();
        }

    }

    // Returns and removes the element at the rear of the queue
    // If the queue is empty, blocks until an element becomes available
    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            // Wait while the queue is empty
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            // Remove and return the element from the queue
            int element = queue.poll();
            // Signal that the queue is not full anymore
            notFull.signal();
            return element;
        } finally {
            lock.unlock();
        }
    }

    // Returns the number of elements currently in the queue
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}