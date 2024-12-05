package server;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Server logger.
 */
public class ServerLogger {

  public void log(String message) {
    String timestamp = getCurrentTime();
    System.out.println("[" + timestamp + "]" + message);
  }

  /**
   * To log a message.
   *
   * @param port     the port number
   * @param address  the client's IP address
   * @param request  the request received from the client
   * @param response the response sent to the client
   */
  public void logMessage(int port, InetAddress address, String request, Response response) {
    String timestamp = getCurrentTime();
    System.out.println("[" + timestamp + "] Received request " + "<" + request + "> " + "from " + address + ":" + port);
    System.out.println("[" + timestamp + "] Response: " + response.formattedResponse());
  }


  /**
   * To log a malformed log message.
   *
   * @param port    the port NUMBER
   * @param length  the length of the malformed request message
   * @param address the client's IP address
   */
  public void malformedLogMsg(int port, int length, InetAddress address) {
    String timestamp = getCurrentTime();
    System.out.println("[" + timestamp + "] Received malformed request of length " + length + " from " + address + ":" + port);
  }

  /**
   * Return the current time formatted with millisecond precision.
   *
   * @return the formatted current time
   */
  private String getCurrentTime() {
    long currentTime = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return sdf.format(new Date(currentTime));
  }

}
