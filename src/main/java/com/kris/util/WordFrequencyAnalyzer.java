package com.kris.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordFrequencyAnalyzer {

    // 正则表达式用于匹配单词
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");

    // 抓取网页内容
    private static String fetchPageContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }

    // 提取单词
    private static List<String> extractWords(String content) {
        List<String> words = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(content.toLowerCase());
        while (matcher.find()) {
            words.add(matcher.group());
        }
        return words;
    }

    // 过滤单词
    private static List<String> filterWords(List<String> words, Set<String> filterSet) {
        return words.stream()
                .filter(word -> !filterSet.contains(word))
                .collect(Collectors.toList());
    }

    // 统计单词频率
    private static Map<String, Integer> countWordFrequency(List<String> words) {

        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : words) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }
        return wordFrequency;
    }

    public static Map<String, Integer> analysis(String url) throws IOException {
        var content = fetchPageContent(url);

        var words = extractWords(content);

        var filteredWords = filterWords(words, Set.of("a"));

        return countWordFrequency(filteredWords);
    }
}
