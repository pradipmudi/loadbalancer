package com.loadbalancer.algorithms;

import com.loadbalancer.config.Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends LoadBalancer {

    private List<Server> servers;
    private AtomicInteger currentIndex;
    private RoundRobinLoadBalancer() {
    }

    public RoundRobinLoadBalancer(List<Server> servers) {
        this.servers = servers;
        this.currentIndex = new AtomicInteger(0);
    }

    @Override
    public Server getNextEligibleServer() {
        if (servers.isEmpty())
            return null;
        int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());
        return servers.get(index);
    }
}