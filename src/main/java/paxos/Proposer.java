package paxos;

import org.apache.thrift.TException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import keyvaluestore.KeyValueStore;
import server.ServerLogger;

public class Proposer {
  private final String proposerId;
  private final List<KeyValueStore.Client> acceptorList; // List of acceptor clients
  private final ServerLogger logger;
  private String proposalId;
  private String highestProposalId = null;
  private String highestAcceptedValue = null;

  public Proposer(String proposerId, List<KeyValueStore.Client> acceptorList, ServerLogger logger) {
    this.proposerId = proposerId;
    this.acceptorList = acceptorList;
    this.logger = logger;
    this.proposalId = createProposalId();
  }

  public boolean propose(String key, String value) {
    try {
      logger.log("Proposer " + proposerId + " initiating Paxos for key: " + key + ", value: " + value);
      logger.log("Phase 1: Sending prepare requests with proposal ID: " + proposalId);
      if (!sendPrepareRequests(key)) {
        logger.log("Phase 1 failed: No majority response for prepare phase.");
        return false;
      }

      // Use highest accepted value if found during prepare phase
      if (highestAcceptedValue != null) {
        value = highestAcceptedValue;
        logger.log("Adopting value from highest proposal seen during prepare phase: " + value);
      }

      // Phase 2: Accept
      logger.log("Phase 2: Sending accept requests with proposal ID: " + proposalId);
      if (!sendAcceptRequests(key, value)) {
        logger.log("Phase 2 failed: No majority response for accept phase.");
        return false;
      }

      // Phase 3: Notify Learners
      logger.log("Phase 3: Notifying learners of the agreed value.");
      notifyLearners(key, value);
      return true;

    } catch (Exception e) {
      logger.log("Error during Paxos process: " + e.getMessage());
      return false;
    }
  }

  private boolean sendPrepareRequests(String key) throws TException {
    int positiveResponses = 0;

    for (KeyValueStore.Client acceptor : acceptorList) {
      try {
        boolean response = acceptor.preparePaxos(proposalId, key, null);
        if (response) {
          positiveResponses++;
        }
        // Handle response for highest accepted proposal
        String currentProposalId = acceptor.getHighestProposalId(); // Assuming this is implemented
        String currentValue = acceptor.getHighestAcceptedValue();  // Assuming this is implemented

        if (currentProposalId != null && (highestProposalId == null || currentProposalId.compareTo(highestProposalId) > 0)) {
          highestProposalId = currentProposalId;
          highestAcceptedValue = currentValue;
          logger.log("Updated highest proposal seen: ID = " + highestProposalId + ", Value = " + highestAcceptedValue);
        }

      } catch (TException e) {
        logger.log("Prepare request failed for acceptor: " + e.getMessage());
      }
    }

    return positiveResponses > (acceptorList.size() / 2);
  }

  private boolean sendAcceptRequests(String key, String value) throws TException {
    int positiveResponses = 0;

    for (KeyValueStore.Client acceptor : acceptorList) {
      try {
        boolean response = acceptor.acceptPaxos(proposalId, key, value);
        if (response) {
          positiveResponses++;
        }
      } catch (TException e) {
        logger.log("Accept request failed for acceptor: " + e.getMessage());
      }
    }

    return positiveResponses > (acceptorList.size() / 2);
  }

  private void notifyLearners(String key, String value) throws TException {
    for (KeyValueStore.Client learner : acceptorList) { // Assuming acceptors also act as learners
      try {
        learner.learnPaxos(key, value);
      } catch (TException e) {
        logger.log("Failed to notify learner: " + e.getMessage());
      }
    }
  }

  public String createProposalId() {
    return System.currentTimeMillis() + "." + proposerId;
  }
}
