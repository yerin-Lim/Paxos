package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Key-value data structure.
 * Perform PUT, GET, and DELETE
 */
public class KeyValue {
  private final Map<String, String> store;
  private final ConcurrentHashMap<String, ReentrantLock> locks;

  /**
   * Constructor for initializing concurrentHashMap
   */
  public KeyValue() {
    store = new ConcurrentHashMap<>();
    locks = new ConcurrentHashMap<>();
  }

  /**
   * To insert a key and a value to the HashMap store.
   *
   * @param key   the key
   * @param value the value
   */
  public void put(String key, String value) {
    store.put(key, value);
  }

  /**
   * To fetch the value that is associated with the key
   *
   * @param key the key
   * @return the value if the key exists
   */
  public String get(String key) {
    return store.get(key);
  }

  /**
   * s
   * Delete a pair of key-value in the HashMap store.
   *
   * @param key the key
   * @return true if the key existed and the key and the value got removed
   */
  public boolean delete(String key) {
    return store.remove(key) != null;
  }

  /**
   * Try to acquire a lock for specific key.
   *
   * @param key the key to lock
   * @return true if the lock was successfully acquired, false otherwise
   */
  public boolean acquireLock(String key) {
    locks.putIfAbsent(key, new ReentrantLock());
    ReentrantLock lock = locks.get(key);
    return lock.tryLock();
  }

  /**
   * Release the lock for a specific key.
   *
   * @param key the key to release the lock
   */
  public void releaseLock(String key) {
    ReentrantLock lock = locks.get(key);
    if (lock != null && lock.isHeldByCurrentThread()) {
      lock.unlock();
    }

  }

}
