package com.loadbalancer.algorithms;

import com.loadbalancer.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LeastConnectionsLoadBalancer extends LoadBalancer {

    private static final Logger logger = LoggerFactory.getLogger(LeastConnectionsLoadBalancer.class);

    private List<ServerConfig> servers;
    private ConcurrentMap<ServerConfig, Integer> connectionsMap;

    // Private constructor to prevent instantiation without servers
    private LeastConnectionsLoadBalancer(){}

    // Constructor to initialize with a list of servers
    public LeastConnectionsLoadBalancer(List<ServerConfig> servers) {
        this.servers = servers;
        this.connectionsMap = new ConcurrentHashMap<>();

        // Initialize connections map with 0 connections for each server
        for (ServerConfig server : servers) {
            connectionsMap.put(server, 0);
        }
    }

    @Override
    public ServerConfig getNextEligibleServer() {
        if (servers.isEmpty()) {
            logger.warn("No servers available");
            return null;
        }

        // Find the server with the least connections
        ServerConfig leastLoadedServerConfig = servers.get(0);
        int minConnections = connectionsMap.get(leastLoadedServerConfig);
        for (ServerConfig server : servers) {
            int connections = connectionsMap.get(server);
            if (connections < minConnections) {
                leastLoadedServerConfig = server;
                minConnections = connections;
            }
        }
        logger.info("Selected server with least connections: {}", leastLoadedServerConfig);
        return leastLoadedServerConfig;
    }

    // Increment the number of connections for the given server
    public void incrementConnections(ServerConfig server) {
        connectionsMap.compute(server, (k, v) -> {
            int newValue = v == null ? 1 : v + 1;
            logger.debug("Incremented connections for server {}: {}", server, newValue);
            return newValue;
        });
    }

    // Decrement the number of connections for the given server
    public void decrementConnections(ServerConfig server) {
        connectionsMap.compute(server, (k, v) -> {
            int newValue = v == null ? 0 : Math.max(0, v - 1);
            logger.debug("Decremented connections for server {}: {}", server, newValue);
            return newValue;
        });
    }
}
