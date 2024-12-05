package client;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Client logger.
 */
public class ClientLogger {
  private String serverAddress;
  private int serverPort;

  /**
   * Instantiates a new Client logger.
   *
   * @param serverAddress the server address
   * @param serverPort    the server port
   */
  public ClientLogger(String serverAddress, int serverPort) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
  }

  /**
   * Log.
   *
   * @param message the message
   */
  public void log(String message) {
    String timestamp = getCurrentTime();
    System.out.println("[" + timestamp + "]" + message);
  }

  /**
   * Log malformed response.
   *
   * @param length the length
   */
  public void logMalformedResponse(int length) {
    String timestamp = getCurrentTime();
    System.out.println("[" + timestamp + "] Received malformed response of length " + length + " from " + serverAddress + ":" + serverPort);
  }

  private String getCurrentTime() {
    long currentTime = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return sdf.format(new Date(currentTime));
  }
}
