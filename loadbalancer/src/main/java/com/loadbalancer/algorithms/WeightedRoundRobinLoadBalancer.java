package com.loadbalancer.algorithms;

import com.loadbalancer.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedRoundRobinLoadBalancer extends LoadBalancer {

    private static final Logger logger = LoggerFactory.getLogger(WeightedRoundRobinLoadBalancer.class);

    private List<ServerConfig> servers;

    public WeightedRoundRobinLoadBalancer(List<ServerConfig> servers) {
        this.servers = servers;
    }

    private WeightedRoundRobinLoadBalancer(){}

    @Override
    public ServerConfig getNextEligibleServer() {
        // Check if the list of servers is empty
        if (servers.isEmpty()) {
            logger.warn("No servers available");
            return null; // If empty, return null since there are no servers available
        }

        // Calculate the total weight of all servers
        int totalWeight = servers.stream().mapToInt(ServerConfig::getWeight).sum();

        // Generate a random number between 0 (inclusive) and totalWeight (exclusive)
        int randomNumber = ThreadLocalRandom.current().nextInt(totalWeight);

        // Iterate through the servers and find the one corresponding to the generated random number
        int currentWeight = 0;
        for (ServerConfig server : servers) {
            // Accumulate the weights of servers until the accumulated weight exceeds the random number
            currentWeight += server.getWeight();
            if (randomNumber < currentWeight) {
                logger.info("Selected server using Weighted Round Robin: {}", server);
                return server; // Return the server whose weight range covers the random number
            }
        }

        // If the loop completes without finding a server (should never happen),
        // return the first server as a fallback
        logger.warn("Failed to select server using Weighted Round Robin, returning the first server as fallback");
        return servers.get(0);
    }
}