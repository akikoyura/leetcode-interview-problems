package com.edu.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawlerMultithreaded {
    public static void main(String[] args) {

    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) throws InterruptedException {
        // extract hostname from startUrl
        String hostName = htmlParser.getHostName(startUrl);

        // Thread-safe set to track visited URLs
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        // Create a thread pool with bounded size instead of unbounded cached pool
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Use CountDownLatch for coordination instead of polling with sleep
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger taskCount = new AtomicInteger(1);

        // Process the starting URL
        executor.submit(() -> crawlUrl(startUrl, hostName, htmlParser, visited, executor, taskCount, latch));

        // Wait until add tasks are completed
        try {
            // Wait for all tasks to complete
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        return new ArrayList<>(visited);
    }

    private void crawlUrl(String url, String hostname, HtmlParser htmlParser, Set<String> visited, ExecutorService executor, AtomicInteger taskCount, CountDownLatch latch) {
        try {
            for (String nextUrl : htmlParser.getUrls(url)) {
                if (htmlParser.getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                    taskCount.incrementAndGet();
                    executor.submit(() -> crawlUrl(nextUrl, hostname, htmlParser, visited, executor, taskCount, latch));
                }
            }
        } finally {
            // if this is the last task, signal completion
            if (taskCount.decrementAndGet() == 0) {
                latch.countDown();
            }
        }
    }

    public interface HtmlParser {
        List<String> getUrls(String html);

        String getHostName(String url);
    }
}

