package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The type Udp handler.
 */
public class UDPHandler extends AbstractHandler {
  /**
   * Instantiates a new Udp handler.
   *
   * @param port   the port
   * @param store  the store
   * @param logger the logger
   */
  public UDPHandler(int port, KeyValue store, ServerLogger logger) {
    super(port, store, logger);
  }

  @Override
  public void start() {
    try (DatagramSocket socket = new DatagramSocket(port)) {
      logger.log("UDP Server started on port " + port);
      byte[] buffer = new byte[1000];
      while (true) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        handlePacket(socket, packet);
      }
    } catch (IOException e) {
      logger.log("IOException in UDP server: " + e.getMessage());
    }
  }

  private void handlePacket(DatagramSocket socket, DatagramPacket packet) {
    String request = new String(packet.getData(), 0, packet.getLength()).trim();
    InetAddress clientAddress = packet.getAddress();
    int clientPortNum = packet.getPort();

    if (request.isEmpty()) {
      malLogMsg(clientPortNum, 0, clientAddress);
      return;
    }

    Response response = handleRequest(request);
    logRequestMsg(clientPortNum, clientAddress, request, response);

    byte[] responseData = response.formattedResponse().getBytes();
    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
    try {
      socket.send(responsePacket);
    } catch (IOException e) {
      logger.log("IOException in UDP Server: " + e.getMessage());
    }
  }
}
