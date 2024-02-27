package com.loadbalancer.algorithms;

import com.loadbalancer.config.ServerConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends LoadBalancer {

    private List<ServerConfig> servers;
    private AtomicInteger currentIndex;
    private RoundRobinLoadBalancer() {
    }

    public RoundRobinLoadBalancer(List<ServerConfig> servers) {
        this.servers = servers;
        this.currentIndex = new AtomicInteger(0);
    }

    @Override
    public ServerConfig getNextEligibleServer() {
        // Check if the list of servers is empty
        if (servers.isEmpty())
            return null; // If empty, return null since there are no servers available

        // Get the current index atomically and update it for the next invocation
        // This atomic operation ensures thread safety when multiple threads access currentIndex concurrently
        int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());

        // Return the server config at the calculated index
        return servers.get(index);
    }

}