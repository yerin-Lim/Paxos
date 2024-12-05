package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * The type Udp client.
 */
public class UDPClient extends AbstractClient{
  private DatagramSocket socket;
  private static final int timeout = 3000;
  private InetAddress inetAddress;

  /**
   * Instantiates a new Udp client.
   *
   * @param serverAddress the server address
   * @param portNum       the port num
   * @param logger        the logger
   */
  public UDPClient(String serverAddress, int portNum, ClientLogger logger) {
    super(serverAddress, portNum, logger);
  }

  @Override
  protected void start() {
    try {
      inetAddress = InetAddress.getByName(serverAddress);
      socket = new DatagramSocket();
      socket.setSoTimeout(timeout);
      logger.log("Connected to UDP Server at " + serverAddress + ":" + portNum);
    } catch (IOException e) {
      logger.log("IOException when connecting to UDP Server: " + e.getMessage());
    }
  }

  @Override
  protected void sendRequest(String request) {
    try {
      byte[] buf = request.getBytes("UTF-8");
      DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, portNum);
      socket.send(packet);
      logger.log("<UDP> Request sent: " + request);
      receiveResponse();
    } catch (IOException e) {
      logger.log("<UDP> Error sending request: " + e.getMessage());
    }
  }

  @Override
  protected void receiveResponse() {
    try {
      byte[] buffer = new byte[1000];
      DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
      socket.receive(responsePacket);
      String response = new String(responsePacket.getData(), 0, responsePacket.getLength(), "UTF-8").trim();
      if (isValidResponse(response)) {
        logger.log("<UDP> Received response: " + response);
      } else {
        logger.logMalformedResponse(responsePacket.getLength());
      }
    } catch (SocketTimeoutException e) {
      logger.log("<UDP> Receive timed out: " + e.getMessage());
    } catch (IOException e) {
      logger.log("<UDP> Error receiving response: " + e.getMessage());
    }
  }

  @Override
  protected void sendPutRequest(String key, String value) {
    sendRequest("PUT " + key + " " + value);
  }

  @Override
  protected void sendGetRequest(String key) {
    sendRequest("GET " + key);
  }

  @Override
  protected void sendDeleteRequest(String key) {
    sendRequest("DELETE " + key);
  }

  private boolean isValidResponse(String response) {
    return response.startsWith("Message: ") && response.contains(" Key: ");
  }

}
