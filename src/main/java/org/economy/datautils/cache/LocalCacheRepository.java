package org.economy.datautils.cache;

import lombok.*;
import org.economy.datautils.Repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@AllArgsConstructor
@RequiredArgsConstructor
public class LocalCacheRepository<K, V> implements CacheRepository<K,V> {

    @Getter
    private final Repository<K, V> dataBase;

    @Getter
    private final ConcurrentHashMap<K, V> memoryCache = new ConcurrentHashMap<>();

    private Function<V, K> keyFunction;

    @Override
    public void cacheAll() {
        if (keyFunction == null) {
            return;
        }

        for (V data : dataBase.fetchAll()) {
            K key = keyFunction.apply(data);
            memoryCache.put(key, data);
        }
    }

    @Override
    public void flushCache() {
        memoryCache.clear();
    }

    @Override
    public Collection<V> fetchAll() {
        return dataBase.fetchAll();
    }

    @Override
    public Collection<V> fetchAllCached() {
        return memoryCache.values();
    }

    @Override
    public V fetchCacheByKey(K key) {
        return memoryCache.get(key);
    }

    @Override
    public V fetchByKey(K key) {
        V data = fetchCacheByKey(key);

        return data != null ? data : fetchSourceByKey(key);
    }

    @Override
    public V fetchSourceByKey(K key) {
        return dataBase.fetchByKey(key);
    }

    @Override
    public V saveData(K key, V data) {
        return dataBase.saveData(key, data);
    }

    @Override
    public V putInCache(K key, V data) {
        if (data == null)
            return null;

        return memoryCache.put(key, data);
    }

    @Override
    public V removeFromCache(K key) {
        return memoryCache.remove(key);
    }

    @Override
    public V removeData(K key) {
        memoryCache.remove(key);
        return dataBase.removeData(key);
    }
}
