package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The type Tcp client.
 */
public class TCPClient extends AbstractClient {
  private DataInputStream in;
  private DataOutputStream out;
  private static final int timeout = 3000;

  /**
   * Instantiates a new Tcp client.
   *
   * @param serverAddress the server address
   * @param portNum       the port num
   * @param logger        the logger
   */
  public TCPClient(String serverAddress, int portNum, ClientLogger logger) {
    super(serverAddress, portNum, logger);
  }

  @Override
  protected void start() {}

  @Override
  protected void sendRequest(String request) {
    try {
      Socket socket = new Socket(serverAddress, portNum);
      socket.setSoTimeout(timeout);
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
      logger.log("Connected to TCP Server at " + serverAddress + ":" + portNum);
      out.writeUTF(request);
      logger.log("<TCP> Request sent: " + request);
      receiveResponse();
    } catch (IOException e) {
      logger.log("<TCP> Error sending request: " + e.getMessage());
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

  @Override
  protected void receiveResponse() {
    try {
      String response = in.readUTF();
      logger.log("<TCP> Received response: " + response);
      in.close();
      out.close();
    } catch (SocketTimeoutException e) {
      logger.log("<TCP> Received timeout: " + e.getMessage());
    } catch (IOException e) {
      logger.log("<TCP> Error receiving response: " + e.getMessage());
    }
  }


}
