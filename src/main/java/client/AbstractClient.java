package client;


/**
 * The type Abstract client.
 */
public abstract class AbstractClient {

  protected int portNum;

  protected ClientLogger logger;

  protected String serverAddress;

  /**
   * Instantiates a new Abstract client.
   *
   * @param serverAddress the server IP address or hostname
   * @param portNum       the server's port number
   * @param logger        the ClientLogger
   */
  public AbstractClient(String serverAddress, int portNum,  ClientLogger logger) {
    this.serverAddress = serverAddress;
    this.portNum = portNum;
    this.logger = logger;
  }

  /**
   * Start.
   */
  protected abstract void start() throws Exception;

  /**
   * Send request to the server.
   *
   * @param request the request
   */
  protected abstract void sendRequest(String request);

  /**
   * Receive response from the server.
   */
  protected abstract void receiveResponse();

  /**
   * Send a put request to store key and value on the server.
   *
   * @param key   the key
   * @param value the value
   */
  protected abstract void sendPutRequest(String key, String value);

  /**
   * Send a get request to retrieve the value associated with the given key.
   *
   * @param key the key
   */
  protected abstract void sendGetRequest(String key);

  /**
   * Send a delete request to delete the given key and the value that is associated with the key.
   *
   * @param key the key
   */
  protected abstract void sendDeleteRequest(String key);

}
