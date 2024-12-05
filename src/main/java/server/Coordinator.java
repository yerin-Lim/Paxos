package server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

import keyvaluestore.KeyValueStore;

public class Coordinator {
  private final List<KeyValueStore.Client> replicas;
  private final long timeout;
  private final ServerLogger logger;
  private final int portNum;

  public Coordinator(int portNum, long timeout, ServerLogger logger) {
    this.portNum = portNum;
    this.timeout = timeout;
    this.logger = logger;
    this.replicas = new ArrayList<>();
  }

  public boolean twoPhaseCommit(String operation, String key, String value, KeyValueThriftService current) throws TException {
    if(this.replicas.size() == 0) {
      connectToReplicas();
    }
    try {
      logger.log("Starting Prepare Phase | Operation:" + operation + ", Key: " + key + ", Value: " + value);
      boolean localPrepared = current.prepare(operation, key, value);
      if (!localPrepared) {
        logger.log("Prepare phase failed on coordinator. Aborting the operation.");
        sendAbortAll(operation, key, value);
        return false;
      }
      for (KeyValueStore.Client replica : replicas) {
        boolean prepared = sendPrepare(replica, operation, key, value);
        if (!prepared) {
          logger.log("Prepare phase failed for the key " + key + ". Aborting the operation.");
          sendAbortAll(operation, key, value);
          return false;
        }
      }
      logger.log("Prepare phase successful for all replicas.");
      logger.log("Starting Commit Phase for Key: " + key);
      current.commit(operation, key, value);
      for (KeyValueStore.Client replica : replicas) {
        sendCommit(replica, operation, key, value);
      }
      logger.log("Commit phase successful for Key: " + key);
      return true;
    } catch (TException e) {
      logger.log("Error during the two phase commit protocol." + e.getMessage());
      sendAbortAll(operation, key, value);
      return false;
    }
  }

  private boolean sendPrepare(KeyValueStore.Client replica, String operation, String key, String value) throws TException {
    final long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < timeout) {
      try {
        return replica.prepare(operation, key, value);
      } catch (TException e) {
        System.err.println("Retrying prepare due to error: " + e.getMessage());
      }
    }
    throw new TException("Prepare request timed out for replica");
  }

  private void sendCommit(KeyValueStore.Client replica, String operation, String key, String value) throws TException {
    final long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < timeout) {
      try {
        replica.commit(operation, key, value);
        return;
      } catch (TException e) {
        System.err.println("Retrying commit due to error: " + e.getMessage());
      }
    }
    throw new TException("Commit request timed out for replica");
  }

  private void sendAbort(KeyValueStore.Client replica, String operation, String key, String value) throws TException {
    final long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < timeout) {
      try {
        replica.abort(operation, key, value);
        logger.log("Abort successful for Key: " + key);
        return;
      } catch (TException e) {
        logger.log("Retrying abort for Key: " + key + " | Error: " + e.getMessage());
      }
    }
    throw new TException("Abort request timed out for Key: " + key);
  }

  private void sendAbortAll(String operation, String key, String value) throws TException {
    for (KeyValueStore.Client replica : replicas) {
      try {
        sendAbort(replica, operation, key, value);
      } catch (TException e) {
        logger.log("Failed to send abort to replica for Key: " + key + " | Error: " + e.getMessage());
      }
    }
  }

  public void connectToReplicas() {
    String host = "127.0.0.1";
    int[] replicaPorts = {9000, 9001, 9002, 9003, 9004};
    long startTime = System.currentTimeMillis();
    this.logger.log("Attemping to connect to replica ports");

    for (int port : replicaPorts) {
      this.logger.log("Attemping to connect to replica port " + port);
      if (port != this.portNum) {
        while (System.currentTimeMillis() - startTime < 10000) {
          try {
            // Create a Thrift client for this replica
            TTransport transport = new TSocket(host, port);
            transport.open();
            KeyValueStore.Client client = new KeyValueStore.Client(new TBinaryProtocol(transport));
            this.replicas.add(client);
            this.logger.log("Connected to replica at " + host + ":" + port);
            break;
          } catch (Exception e) {
            this.logger.log("Failed to connect to replica at " + host + ":" + port + " | Error: " + e.getMessage());
            try {
              Thread.sleep(1000);
            } catch (InterruptedException exception) {
              Thread.currentThread().interrupt();
            }
          }
        }

      }
    }
  }
}
