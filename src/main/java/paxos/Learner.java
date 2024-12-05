package paxos;

import server.KeyValue;

public class Learner {
  private final KeyValue keyValueStore;

  public Learner(KeyValue keyValueStore) {
    this.keyValueStore = keyValueStore;
  }

  public void learn(String key, String value) {
    keyValueStore.put(key, value);
  }
}
