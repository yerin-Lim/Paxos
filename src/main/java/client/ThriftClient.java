package client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import keyvaluestore.InvalidOperation;
import keyvaluestore.KeyValueStore;

public class ThriftClient extends AbstractClient {

  private KeyValueStore.Client client;
  private TTransport transport;

  public ThriftClient(String serverAddress, int portNum, ClientLogger logger){
    super(serverAddress, portNum, logger);
  }

  @Override
  protected void start() throws TTransportException {
    try {
      transport = new TSocket(serverAddress, portNum);
      transport.open();
      TProtocol protocol = new TBinaryProtocol(transport);
      client = new KeyValueStore.Client(protocol);
      logger.log("Connected to Thrift server at " + serverAddress + ":" + portNum );
    } catch (TTransportException e) {
      logger.log("TTransportException when connecting to Thrift Server: " + e.getMessage());
    }
  }

  @Override
  protected void sendPutRequest(String key, String value) {
    try {
      logger.log("Sending PUT request | Key: " + key + " Value: " + value);
      String response = client.put(key, value);
      logger.log(response);
    } catch (InvalidOperation e) {
      logger.log(e.toString());
    } catch (TException e) {
      logger.log(e.toString());
    }
  }

  @Override
  protected void sendGetRequest(String key) {
    try {
      logger.log("Sending GET request | Key: " + key);
      String response = client.get(key);
      logger.log(response);
    } catch (InvalidOperation e) {
      logger.log(e.toString());
    } catch (TException e) {
      logger.log(e.toString());
    }
  }

  @Override
  protected void sendDeleteRequest(String key) {
    try {
      logger.log("Sending DELETE request | Key: " + key);
      String response = client.delete(key);
      logger.log(response);
    } catch (Exception e) {
      logger.log(e.toString());
    }
  }

  @Override
  protected void sendRequest(String request) {
  }

  @Override
  protected void receiveResponse() {
  }
}
