package server;

import paxos.Acceptor;
import paxos.Learner;
import paxos.Proposer;

/**
 * The type Server app.
 */
public class ServerApp {
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Please enter <protocol> <port-number>");
      System.err.println("Example: java ServerApp tcp 8080");
      return;
    }

    String protocol = args[0].toLowerCase();
    int portNum;

    try {
      portNum = Integer.parseInt(args[1]);

      if (!isValid(portNum)) {
        System.err.println("Please provide a port number between 9000 and 9004");
        return;
      }

    } catch (NumberFormatException e){
        System.err.println("Please provide valid integer");
        return;
    }

    KeyValue store = new KeyValue();
    ServerLogger logger = new ServerLogger();
    //prepopulated data
    store.put("prepopulatedKey1", "prepopulatedValue1");
    store.put("prepopulatedKey2", "prepopulatedValue2");
    store.put("prepopulatedKey3", "prepopulatedValue3");
    store.put("KEY3", "VALUE3");

    switch (protocol) {
      case "tcp":
        TCPHandler tcpHandler = new TCPHandler(portNum, store, logger);
        logger.log("Starting TCPHandler on port " + portNum);
        tcpHandler.start();
        break;
      case "udp":
        UDPHandler udpHandler = new UDPHandler(portNum, store, logger);
        logger.log("Starting UDPHandler on port " + portNum);
        udpHandler.start();
        break;

      case "thrift":
        Proposer proposer = new Proposer(logger);
        Acceptor acceptor = new Acceptor(logger);
        Learner learner = new Learner(logger);
        ThriftHandler thriftHandler = new ThriftHandler(portNum, store, logger, proposer, acceptor, learner);
        logger.log("Starting ThriftHandler on port " + portNum);
        thriftHandler.start();
        break;

      default:
        System.err.println("Please enter the available protocol; 'tcp', 'udp' or 'thrift");
    }

  }
  /**
   * To check the validity of the port number.
   *
   * @param port the input integer
   */
  private static boolean isValid(int port) {
    return 9000 <= port && port <= 9004;
  }

}
