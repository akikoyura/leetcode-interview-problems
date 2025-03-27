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

public class BoundedBlockingQueue {

    // Constructor initializes the queue with a maximum capacity
    public BoundedBlockingQueue(int capacity) {
    }

    // Adds an element to the front of the queue
    // If the queue is full, blocks until space becomes available
    public void enqueue(int element) throws InterruptedException {
    }

    // Returns and removes the element at the rear of the queue
    // If the queue is empty, blocks until an element becomes available
    public int dequeue() throws InterruptedException {
        return 0;
    }

    // Returns the number of elements currently in the queue
    public int size() {
        return 0;
    }
}