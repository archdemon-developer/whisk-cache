package com.whiskcache.persistence;


import com.whiskcache.entry.CacheEntry;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilePersistenceProvider<K, V> implements PersistenceProvider<K, V> {
    private final String filePath;

    public FilePersistenceProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(Map<K, CacheEntry<V>> data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, CacheEntry<V>> load() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) return new ConcurrentHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<K, CacheEntry<V>>) ois.readObject();
        }
    }
}
