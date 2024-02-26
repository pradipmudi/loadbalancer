package com.loadbalancer.algorithms;

import com.loadbalancer.config.Server;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LeastConnectionsLoadBalancer extends LoadBalancer {

    private List<Server> servers;
    private ConcurrentMap<Server, Integer> connectionsMap;

    private LeastConnectionsLoadBalancer(){}
    public LeastConnectionsLoadBalancer(List<Server> servers) {
        this.servers = servers;
        this.connectionsMap = new ConcurrentHashMap<>();
        for (Server server : servers) {
            connectionsMap.put(server, 0);
        }
    }

    @Override
    public Server getNextEligibleServer() {
        if (servers.isEmpty())
            return null;

        Server leastLoadedServer = servers.get(0);
        int minConnections = connectionsMap.get(leastLoadedServer);
        for (Server server : servers) {
            int connections = connectionsMap.get(server);
            if (connections < minConnections) {
                leastLoadedServer = server;
                minConnections = connections;
            }
        }
        return leastLoadedServer;
    }

    public void incrementConnections(Server server) {
        connectionsMap.compute(server, (k, v) -> v == null ? 1 : v + 1);
    }

    public void decrementConnections(Server server) {
        connectionsMap.compute(server, (k, v) -> v == null ? 0 : Math.max(0, v - 1));
    }
}