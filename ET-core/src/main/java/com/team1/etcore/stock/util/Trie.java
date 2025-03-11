package com.team1.etcore.stock.util;

import com.team1.etcore.stock.dto.StockRes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;

public class Trie {

    private final TrieNode root = new TrieNode();

    public void insert(String key, StockRes stock) {
        TrieNode node = root;
        for (char ch : key.toCharArray()) {
            node = node.getChildren().computeIfAbsent(ch, c -> new TrieNode());

            node.getStocks().add(stock);
        }
    }

    public Set<StockRes> search(String key) {
        TrieNode node = root;

        if(key.matches("[a-zA-Z]+")) {
            key = key.toUpperCase();
        }
        for (char ch : key.toCharArray()) {
            node = node.getChildren().get(ch);
            if (Objects.isNull(node)) {
                return new HashSet<>();
            }
        }
        return node.getStocks();
    }

    // TrieNode 내부 클래스
    @Getter
    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        @Getter
        private final Set<StockRes> stocks = new HashSet<>();

    }
}