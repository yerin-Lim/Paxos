package server;

import org.apache.thrift.TException;

import keyvaluestore.InvalidOperation;
import keyvaluestore.KeyValueStore;
import keyvaluestore.Operation;
import paxos.Acceptor;
import paxos.Learner;
import paxos.Proposer;

public class KeyValueThriftService implements KeyValueStore.Iface {
  private final KeyValue store;
  private final ServerLogger logger;
  private final Proposer proposer;
  private final Acceptor acceptor;
  private final Learner learner;

  public KeyValueThriftService(KeyValue store, ServerLogger logger, Proposer proposer, Acceptor acceptor, Learner learner) {
    this.store = store;
    this.logger = logger;
    this.proposer = proposer;
    this.acceptor = acceptor;
    this.learner = learner;
  }

  @Override
  public String put(String key, String value) throws InvalidOperation, TException {
    if (key == null || key.isEmpty()) {
      logger.log("PUT failed: Key cannot be null or empty.");
      throw new InvalidOperation(Operation.PUT, "Key cannot be null or empty.");
    }
    if (value == null || value.isEmpty()) {
      logger.log("PUT failed: Value cannot be null or empty.");
      throw new InvalidOperation(Operation.PUT, "Value cannot be null or empty.");
    }

    if (key.length() >= 200) {
      logger.log("PUT failed: Key length exceeds 200 characters. Key: " + key);
      throw new InvalidOperation(Operation.PUT, "Key length must be under 200 characters.");
    }
    if (value.length() >= 200) {
      logger.log("PUT failed: Value length exceeds 200 characters. Value: " + value);
      throw new InvalidOperation(Operation.PUT, "Value length must be under 200 characters.");
    }

    String proposalId = proposer.createProposalId();
    boolean prepared = preparePaxos(proposalId, key, value);
    if (!prepared) {
      logger.log("PUT failed during Paxos prepare phase.");
      throw new InvalidOperation(Operation.PUT, "Paxos prepare phase failed.");
    }

    boolean accepted = acceptPaxos(proposalId, key, value);
    if (!accepted) {
      logger.log("PUT failed during Paxos accept phase.");
      throw new InvalidOperation(Operation.PUT, "Paxos accept phase failed.");
    }

    learnPaxos(key, value);
    String msg = "PUT operation successful | Key: " + key + ", Value: " + value;
    logger.log(msg);
    return msg;
  }

  @Override
  public String get(String key) throws TException {
    if (key == null || key.isEmpty()) {
      logger.log("GET failed: Key cannot be null or empty.");
      throw new InvalidOperation(Operation.GET, "Key cannot be null or empty.");
    }

    if (key.length() >= 200) {
      logger.log("GET failed: Key length must be under 200 characters." + " | Key: " + key);
      throw new InvalidOperation(Operation.GET, "Key length must be under 200 characters.");
    }

    String result = store.get(key);
    if (result == null) {
      logger.log("GET failed: Key not found | Key: " + key);
      throw new InvalidOperation(Operation.GET, "Key not found: " + key);
    }
    String msg = "GET operation successful | Key: " + key + ", Value: " + result;
    logger.log(msg);
    return msg;
  }

  @Override
  public String delete(String key) throws TException {
    if (key == null || key.isEmpty()) {
      logger.log("DELETE failed: Key cannot be null or empty.");
      throw new InvalidOperation(Operation.DELETE, "Key cannot be null or empty.");
    }

    if (key.length() >= 200) {
      logger.log("DELETE failed: Key length must be under 200 characters." + " | Key: " + key);
      throw new InvalidOperation(Operation.DELETE, "Key length must be under 200 characters.");
    }

    String proposalId = proposer.createProposalId();
    boolean prepared = preparePaxos(proposalId, key, null);
    if (!prepared) {
      logger.log("DELETE failed during Paxos prepare phase.");
      throw new InvalidOperation(Operation.DELETE, "Paxos prepare phase failed.");
    }

    boolean accepted = acceptPaxos(proposalId, key, null);
    if (!accepted) {
      logger.log("DELETE failed during Paxos accept phase.");
      throw new InvalidOperation(Operation.DELETE, "Paxos accept phase failed.");
    }

    learnPaxos(key, null);
    String msg = "DELETE operation successful | Key: " + key;
    logger.log(msg);
    return msg;
  }

  @Override
  public boolean preparePaxos(String proposalId, String key, String value) throws TException {
    logger.log("Paxos <Prepare phase> started | ProposalID: " + proposalId + ", Key: " + key + ", Value: " + value);
    return acceptor.prepare(proposalId);
  }

  @Override
  public boolean acceptPaxos(String proposalId, String key, String value) throws TException {
    logger.log("Paxos <Accept phase> started | ProposalID: " + proposalId + ", Key: " + key + ", Value: " + value);
    return acceptor.accept(proposalId, value);
  }

  @Override
  public void learnPaxos(String key, String value) throws TException {
    logger.log("Paxos <Learn phase> started | Key: " + key + ", Value: " + value);
    learner.learn(key, value);
  }
}
