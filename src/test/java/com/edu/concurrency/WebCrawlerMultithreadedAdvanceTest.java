package com.edu.concurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class WebCrawlerMultithreadedAdvanceTest {

    @Test
    public void testSimpleWebsite() {
        // A simple website with a few pages
        MockHtmlParser parser = new MockHtmlParser();

        // Create website structure
        parser.addPage("http://example.com/", Arrays.asList(
            "http://example.com/about",
            "http://example.com/contact"
        ));
        parser.addPage("http://example.com/about", List.of(
                "http://example.com/team"
        ));
        parser.addPage("http://example.com/contact", Collections.emptyList());
        parser.addPage("http://example.com/team", List.of(
                "http://example.com/"
        ));

        WebCrawlerMultithreadedAdvance crawler = new WebCrawlerMultithreadedAdvance();
        List<String> result = crawler.crawl("http://example.com/", parser);

        assertEquals(4, result.size());
        assertTrue(result.contains("http://example.com/"));
        assertTrue(result.contains("http://example.com/about"));
        assertTrue(result.contains("http://example.com/contact"));
        assertTrue(result.contains("http://example.com/team"));
    }

    @Test
    public void testExternalLinks() {
        // A website with internal and external links
        MockHtmlParser parser = new MockHtmlParser();

        parser.addPage("http://example.com/", Arrays.asList(
            "http://example.com/about",
            "http://external.com/page"
        ));
        parser.addPage("http://example.com/about", Arrays.asList(
            "http://another.com/",
            "http://example.com/contact"
        ));
        parser.addPage("http://example.com/contact", Collections.emptyList());

        WebCrawlerMultithreadedAdvance crawler = new WebCrawlerMultithreadedAdvance();
        List<String> result = crawler.crawl("http://example.com/", parser);

        assertEquals(3, result.size());
        assertTrue(result.contains("http://example.com/"));
        assertTrue(result.contains("http://example.com/about"));
        assertTrue(result.contains("http://example.com/contact"));
    }

    @Test
    public void testDeepStructure() {
        // Test deep nesting of pages
        MockHtmlParser parser = new MockHtmlParser();

        parser.addPage("http://example.com/", List.of("http://example.com/level1"));
        parser.addPage("http://example.com/level1", List.of("http://example.com/level2"));
        parser.addPage("http://example.com/level2", List.of("http://example.com/level3"));
        parser.addPage("http://example.com/level3", List.of("http://example.com/level4"));
        parser.addPage("http://example.com/level4", List.of("http://example.com/level5"));
        parser.addPage("http://example.com/level5", Collections.emptyList());

        WebCrawlerMultithreadedAdvance crawler = new WebCrawlerMultithreadedAdvance();
        List<String> result = crawler.crawl("http://example.com/", parser);

        assertEquals(6, result.size());
    }

    // Mock implementation of HtmlParser
    private static class MockHtmlParser implements WebCrawlerMultithreaded.HtmlParser {
        private final Map<String, List<String>> websiteGraph = new HashMap<>();

        public void addPage(String url, List<String> links) {
            websiteGraph.put(url, links);
        }

        @Override
        public List<String> getUrls(String html) {
            return websiteGraph.getOrDefault(html, Collections.emptyList());
        }

        @Override
        public String getHostName(String url) {
            try {
                return url.substring(7, url.indexOf('/', 7));
            } catch (Exception e) {
                return "";
            }
        }
    }
}