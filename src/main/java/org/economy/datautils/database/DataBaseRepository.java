package org.economy.datautils.database;

import org.economy.datautils.Repository;

import java.util.Collection;
import java.util.List;

public class DataBaseRepository<K, V> implements Repository<K, V> {

    @Override
    public Collection<V> fetchAll() {
        return List.of();
    }

    @Override
    public V fetchByKey(K key) {
        return null;
    }

    @Override
    public V saveData(K key, V data) {
        return null;
    }

    @Override
    public V removeData(K key) {
        return null;
    }
}
