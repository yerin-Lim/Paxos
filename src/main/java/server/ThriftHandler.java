package server;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import keyvaluestore.KeyValueStore;
import paxos.Acceptor;
import paxos.Learner;
import paxos.Proposer;

public class ThriftHandler extends AbstractHandler {

  private final Proposer proposer;
  private final Acceptor acceptor;
  private final Learner learner;

  public ThriftHandler (int port, KeyValue store, ServerLogger logger, Proposer proposer, Acceptor acceptor, Learner learner) {
    super(port, store, logger);
    this.proposer = proposer;
    this.acceptor = acceptor;
    this.learner = learner;
  }

  @Override
  public void start() {
    try {
      KeyValueThriftService service = new KeyValueThriftService(store, logger, proposer, acceptor, learner);
      KeyValueStore.Processor<KeyValueThriftService> processor = new KeyValueStore.Processor<>(service);
      TServerTransport serverTransport = new TServerSocket(port);
      TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
      logger.log("Thrift Server starting on port " + port);
      server.serve();
    } catch (TException e) {
      logger.log("TTransportException on Thrift server: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
