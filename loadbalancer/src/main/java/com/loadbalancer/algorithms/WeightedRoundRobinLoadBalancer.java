package com.loadbalancer.algorithms;

import com.loadbalancer.config.Server;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinLoadBalancer extends LoadBalancer {

    private List<Server> servers;

    public WeightedRoundRobinLoadBalancer(List<Server> servers) {
        this.servers = servers;
    }

    private WeightedRoundRobinLoadBalancer(){}

    @Override
    public Server getNextEligibleServer() {
        if (servers.isEmpty())
            return null;

        int totalWeight = servers.stream().mapToInt(Server::getWeight).sum();
        int randomNumber = ThreadLocalRandom.current().nextInt(totalWeight);

        int currentWeight = 0;
        for (Server server : servers) {
            currentWeight += server.getWeight();
            if (randomNumber < currentWeight)
                return server;
        }

        // Should never reach this point
        return servers.get(0);
    }
}