package client;

import java.util.Scanner;

/**
 * The type Client app.
 */
public class ClientApp {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.out.println("Please enter <protocol> <server-address> <port-number>");
      System.out.println("Example: java client.ClientApp UDP localhost 9090");
      return;
    }
    String protocol = args[0].toLowerCase();
    String serverAddress = args[1];
    int portNum;


    try {
      portNum = Integer.parseInt(args[2]);
      if (!isValid(portNum)) {
        System.err.println("Please provide a port number between 1 and 65535");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Please provide valid integer");
      return;
    }

    ClientLogger logger = new ClientLogger(serverAddress, portNum);

    AbstractClient client;
    switch (protocol) {
      case "tcp":
        client = new TCPClient(serverAddress, portNum, logger);
        logger.log("Starting TCPClient on port " + portNum);
        break;
      case "udp":
        client = new UDPClient(serverAddress, portNum, logger);
        logger.log("Starting UDPClient on port " + portNum);
        break;
      case "thrift":
        client = new ThriftClient(serverAddress, portNum, logger);
        logger.log("Starting ThriftClient on port " + portNum);
        break;
      default:
        System.err.println("Please enter the available protocol; 'tcp' or 'udp'.");
        return;
    }

    client.start();

    client.sendPutRequest("KEY1", "VALUE1");
    client.sendPutRequest("KEY2", "VALUE2");

    client.sendPutRequest("KEY3", "VALUE333"); //Override the existed key's value
    client.sendPutRequest("KEY4", "VALUE4");
    client.sendPutRequest("KEY5", "VALUE5");

    client.sendGetRequest("KEY1");
    client.sendGetRequest("KEY2");
    client.sendGetRequest("KEY3");

    client.sendGetRequest("KEY4");

    client.sendGetRequest("prepopulatedKey1"); //Check prepopulated data in the server
    client.sendDeleteRequest("KEY1");

    client.sendDeleteRequest("KEY2");
    client.sendDeleteRequest("KEY3");

    client.sendDeleteRequest("KEY4");
    client.sendDeleteRequest("prepopulatedKey2");

    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter Commands: PUT <key> <value> | GET <key> | DELETE <key> | Exit");
    while (true) {
      String command = scanner.nextLine().trim();

      if (command.equalsIgnoreCase("exit")) {
        System.out.println("Exiting..");
        break;
      }

      String[] input = command.split("\\s+", 3);
      if (input.length < 2) {
        System.out.println("Invalid command. Please enter valid command.");
        System.out.println("Example: PUT <key> <value> | GET <key> | DELETE <key> | Exit");
        continue;
      }

      String operation = input[0].toUpperCase();
      String key = input[1];

      if(operation.equals("PUT") && input.length < 3) {
        System.out.println("Invalid command. Please enter valid command.");
        System.out.println("Example: PUT <key> <value> | GET <key> | DELETE <key> | Exit");
        continue;
      }

      switch (operation) {
        case "PUT":
          String value = input[2];
          client.sendPutRequest(key, value);
          break;
        case "GET":
          client.sendGetRequest(key);
          break;
        case "DELETE":
          client.sendDeleteRequest(key);
          break;
        default:
          System.out.println("Invalid command.");
      }
    }
    scanner.close();
  }
  /**
   * To check the validity of the port number.
   *
   * @param port the input integer
   */
  private static boolean isValid(int port) {
    return 1 <= port && port <= 65535;
  }

  private static void sleep() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
      // you probably want to quit if the thread is interrupted
      return;

    }
  }

}
