package paxos;

import java.util.concurrent.locks.ReentrantLock;
import server.ServerLogger;

public class Acceptor {

  private String highestProposalId;  // The highest proposal ID this Acceptor has seen
  private String acceptedProposalId; // The proposal ID of the value this Acceptor has accepted
  private String acceptedValue;      // The value this Acceptor has accepted
  private final ReentrantLock lock;  // Lock to ensure thread safety
  private final ServerLogger logger; // Logger for debugging and tracing

  public Acceptor(ServerLogger logger) {
    this.highestProposalId = null;
    this.acceptedProposalId = null;
    this.acceptedValue = null;
    this.lock = new ReentrantLock();
    this.logger = logger;
  }

  /**
   * Handle the prepare phase of Paxos.
   *
   * @param proposalId The ID of the incoming proposal.
   * @return true if this Acceptor promises not to accept lower proposals.
   */
  public boolean prepare(String proposalId) {
    lock.lock();
    try {
      if (highestProposalId == null || proposalId.compareTo(highestProposalId) > 0) {
        logger.log("Acceptor: Promise to proposal ID " + proposalId + " (Previous highest: " + highestProposalId + ")");
        highestProposalId = proposalId;
        return true;
      } else {
        logger.log("Acceptor: Reject proposal ID " + proposalId + " (Higher proposal already seen: " + highestProposalId + ")");
        return false;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Handle the accept phase of Paxos.
   *
   * @param proposalId The ID of the incoming proposal.
   * @param value      The value associated with the proposal.
   * @return true if the proposal is accepted.
   */
  public boolean accept(String proposalId, String value) {
    lock.lock();
    try {
      if (highestProposalId == null || proposalId.compareTo(highestProposalId) >= 0) {
        highestProposalId = proposalId;
        acceptedProposalId = proposalId;
        acceptedValue = value;
        logger.log("Acceptor: Accepted proposal ID " + proposalId + " with value: " + value);
        return true;
      } else {
        logger.log("Acceptor: Reject accept request for proposal ID " + proposalId + " (Higher proposal already seen: " + highestProposalId + ")");
        return false;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Get the highest proposal ID seen by this Acceptor.
   *
   * @return The highest proposal ID.
   */
  public String getHighestProposalId() {
    lock.lock();
    try {
      return highestProposalId;
    } finally {
      lock.unlock();
    }
  }

  /**
   * Get the value accepted by this Acceptor.
   *
   * @return The accepted value.
   */
  public String getAcceptedValue() {
    lock.lock();
    try {
      return acceptedValue;
    } finally {
      lock.unlock();
    }
  }
}
