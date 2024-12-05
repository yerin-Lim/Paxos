package server;

import java.net.InetAddress;

/**
 * The type Abstract handler.
 */
public abstract class AbstractHandler {
  /**
   * The Port.
   */
  protected int port;
  /**
   * The Store.
   */
  protected KeyValue store;
  /**
   * The Logger.
   */
  protected ServerLogger logger;

  /**
   * Instantiates a new Abstract handler.
   *
   * @param port   the port
   * @param store  the store
   * @param logger the logger
   */
  public AbstractHandler(int port, KeyValue store, ServerLogger logger) {
    this.port = port;
    this.store = store;
    this.logger = logger;
  }

  /**
   * Handle request response.
   *
   * @param request the request
   * @return the response
   */
  protected Response handleRequest(String request) {
    if (request == null || request.trim().isEmpty()) {
      return new Response("Request is empty.", "Error", null);
    }
    String[] input = request.trim().split("\\s+", 3);
    String operation = input[0];
    String key = input[1];
    String value;
    try {
      value = input[2];
    } catch (ArrayIndexOutOfBoundsException e) {
      value = null;
    }

    if (key == null || key.isEmpty()) {
      return new Response("Please contain a key which is String.", "Error", null);
    }
    if (!isValidStringLength(key)) {
      return new Response("Please contain a key which is less than 200 characters.", "Error", null);
    }


    switch (operation) {
      case "PUT":
        if (value == null || value.isEmpty()) {
          return new Response("PUT operation requires a value which is String.", key, null);
        }
        if (!isValidStringLength(value)) {
          return new Response("Please contain a value which is less than 200 characters.", "Error", null);
        }
        store.put(key, value);
        return new Response("PUT operation successful.", key, value);

      case "GET":
        String foundValue = store.get(key);
        if (foundValue != null) {
          return new Response("GET operation successful.", key, foundValue);
        } else {
          return new Response("Key not found.", key, null);
        }

      case "DELETE":
        if(store.delete(key)) {
          return new Response("DELETE operation successful.", key, null);
        } else {
          return new Response("Key not found.", key, null);
        }

      default:
        return new Response("The entered operation is not available. Use 'PUT', 'GET', or 'DELETE'.", key, null);
    }

  }

  /**
   * Log request msg.
   *
   * @param port     the port
   * @param address  the address
   * @param request  the request
   * @param response the response
   */
  protected void logRequestMsg(int port, InetAddress address, String request, Response response) {
    logger.logMessage(port, address, request, response);
  }

  /**
   * Mal log msg.
   *
   * @param port    the port
   * @param length  the length
   * @param address the address
   */
  protected void malLogMsg(int port, int length, InetAddress address) {
    logger.malformedLogMsg(port, length, address);
  }

  /**
   * Start.
   */
  protected void start() {

  }

  private boolean isValidStringLength(String value) {
    return value.length() < 200;
  }

}
