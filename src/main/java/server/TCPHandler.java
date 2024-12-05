package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The type Tcp handler.
 */
public class TCPHandler extends AbstractHandler{
  /**
   * Instantiates a new Tcp handler.
   *
   * @param port   the port
   * @param store  the store
   * @param logger the logger
   */
  public TCPHandler(int port, KeyValue store, ServerLogger logger) {
    super(port, store, logger);
  }

  @Override
  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      logger.log("TCP Server started on port: " + port);
      while (true) {
        Socket clientSocket = serverSocket.accept();
        InetAddress clientAddress = clientSocket.getInetAddress();
        int clientPortNum = clientSocket.getPort();
        logger.log("Connected from " + clientAddress.getHostAddress() + ":" + clientPortNum);
        handleClient(clientSocket);
      }
    } catch (IOException e) {
        logger.log("IOException in TCP Server" + e.getMessage());
    }
  }

  private void handleClient(Socket clientSocket) {
    try (

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
      ) {
      String request = in.readUTF();

      if (request == null || request.trim().isEmpty()) {
        malLogMsg(clientSocket.getPort(), 0, clientSocket.getInetAddress());
      }

      Response response = handleRequest(request);
      logRequestMsg(clientSocket.getPort(), clientSocket.getInetAddress(), request, response);
      out.writeUTF(response.formattedResponse());

    } catch (IOException e) {
      logger.log("IOException in TCP Server" + e.getMessage());
    }
  }
}
