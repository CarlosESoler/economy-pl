package org.economy.datautils.cache;

import org.economy.datautils.Repository;

import java.util.Collection;

public interface CacheRepository<K, V> extends Repository<K, V> {
    void cacheAll();
    void flushCache();
    Collection<V> fetchAllCached();
    V fetchCacheByKey(K key);
    V fetchSourceByKey(K key);
    V putInCache(K key, V data);
    V removeFromCache(K key);
}
