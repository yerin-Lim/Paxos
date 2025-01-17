
[Description]

This project aims to implement a client-server architecture for a key-value store application that uses the Two-Phase Commit (2PC) protocol. It is designed to handle multiple replicas of the Key-Value store and handle fundamental operations—PUT, GET, and DELETE across these replicas with ensuring data consistency.

[Requirements]

Install Docker

[Steps]
1. Unzip the provided zip file. 

2. Open two Command Prompts, navigate to the folder that source codes are unzipped to.

3. Build the client docker container with the following command:

`docker build -t yerinl/thriftclient -f Dockerfile.client .`

4. On one terminal, run the server replicas using the below command.
`docker-compose -f docker-compose.yml up`

5. On the other terminal, run the client using the below command with the protocol, IP address, the port number. The port number needs to be between 9000 and 9004.

`docker run --net="host" -i yerinl/thriftclient:latest <portNum>`

So an example would be to run 

`docker run --net="host" -i yerinl/thriftclient:latest 9000`

[Executive Summary]

Project 3 extends Project 2 by deploying 5 server replicas of the Key-Value Store and using 2PC protocol to coordinate the PUT and DELETE operations. Each server replica should handle clients' requests independently while it ensures data consistency across all replicas. A client can send a request to any replica server and perform operations. There are two phases; prepare and commit in the 2PC protocol. In the Prepare phase, all replicas must agree on the operation before committing. In the Commit phase, the operation is committed when all replicas are prepared. The Coordinator ensures consistency across the replicas following the 2PC protocol.  It initiates and manages the protocol by executing the prepare and commit phases. It communicates with replicas using methods like prepare, commit, and abort. Additionally, to ensure the data concurrency, ReentrantLock is used. It prevents concurrent modification so that it allows threads to access shared resources safely. From the assignment requirement, we assumed that no servers will fail, but I implemented timeouts to prevent issues. To deploy this project, I used Docker and Docker Compose. 

It was an interesting project that required multiple replica servers and implemented the Two-Phase Commit protocol. One of the most challenging parts was understanding and implementing the 2PC protocol. I had to learn about each phase and how they work together. Another problem was maintaining the data consistency and concurrency across all replica servers. I had to make sure that when clients requested PUT or DELETE operations, it was important to ensure mutual exclusion. I decided to use a lock to avoid a deadlock. Also, another challenge was deploying the project using Docker. It took a lot of time to figure out how Docker Compose works to run multiple server instances. I had to make sure that all five replicas communicate and connect properly.
Additionally, from this project, I realized again how important the logs are. While I was debugging, having clear and detailed logs helped me identify and fix issues. They gave me a better understanding of how the replicas interacted with one another. Lastly, at the beginning of the project, there were some questions on the requirements of the project. For example, how clients can connect to one of the five servers. From the Piazza, I could solidify my understanding of the assignment and clarify what I should implement.#   P a x o s  
 # Paxos
