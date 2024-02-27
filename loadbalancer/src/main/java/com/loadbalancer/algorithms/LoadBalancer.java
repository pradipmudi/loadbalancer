package com.loadbalancer.algorithms;

import com.loadbalancer.config.ServerConfig;

public abstract class LoadBalancer {

    public ServerConfig getNextEligibleServer() {
        return null;
    }
}
