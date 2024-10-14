package org.economy.datautils;

import java.util.Collection;

public interface Repository<K, V> {
    Collection<V> fetchAll();
    V fetchByKey(K key);
    V saveData(K key, V data);
    V removeData(K key);
}
