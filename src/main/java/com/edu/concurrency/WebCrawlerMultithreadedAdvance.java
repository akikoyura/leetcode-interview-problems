package com.edu.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Phaser;
import java.util.concurrent.RecursiveAction;

public class WebCrawlerMultithreadedAdvance {
    public List<String> crawl(String startUrl, WebCrawlerMultithreaded.HtmlParser htmlParser) {
        String hostname = htmlParser.getHostName(startUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        ForkJoinPool pool = new ForkJoinPool();
        Phaser phaser = new Phaser(1); // Register the main task

        pool.invoke(new CrawlTask(startUrl, hostname, htmlParser, visited, phaser));

        phaser.arriveAndAwaitAdvance(); // Wait for all tasks to complete
        pool.shutdown();

        return new ArrayList<>(visited);
    }

    private static class CrawlTask extends RecursiveAction {
        private final String url;
        private final String hostname;
        private final WebCrawlerMultithreaded.HtmlParser htmlParser;
        private final Set<String> visited;
        private final Phaser phaser;

        CrawlTask(String url, String hostname, WebCrawlerMultithreaded.HtmlParser htmlParser, Set<String> visited, Phaser phaser) {
            this.url = url;
            this.hostname = hostname;
            this.htmlParser = htmlParser;
            this.visited = visited;
            this.phaser = phaser;
        }

        @Override
        protected void compute() {
            try {
                List<CrawlTask> tasks = new ArrayList<>();
                for (String nextUrl : htmlParser.getUrls(url)) {
                    if (htmlParser.getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                        phaser.register(); // Register a new task
                        tasks.add(new CrawlTask(nextUrl, hostname, htmlParser, visited, phaser));
                    }
                }
                invokeAll(tasks); // Invoke all subtasks
            } finally {
                phaser.arriveAndDeregister(); // Deregister the task
            }
        }
    }

    public interface HtmlParser {
        List<String> getUrls(String html);

        String getHostName(String url);
    }
}
