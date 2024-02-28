package com.loadbalancer.factory;

import com.loadbalancer.algorithms.LeastConnectionsLoadBalancer;
import com.loadbalancer.algorithms.LoadBalancer;
import com.loadbalancer.algorithms.RoundRobinLoadBalancer;
import com.loadbalancer.algorithms.WeightedRoundRobinLoadBalancer;
import com.loadbalancer.config.LoadBalancerConfiguration;
import com.loadbalancer.config.ServerConfig;
import com.loadbalancer.constant.LoadBalancingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadBalancerFactory {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerFactory.class);
    private final LoadBalancerConfiguration loadBalancerConfiguration;

    @Autowired
    public LoadBalancerFactory(LoadBalancerConfiguration loadBalancerConfiguration) {
        this.loadBalancerConfiguration = loadBalancerConfiguration;
    }

    public LoadBalancer getConfiguredLoadBalancingAlgorithm() {
        LoadBalancingAlgorithm selectedAlgorithm = loadBalancerConfiguration.getSelectedAlgorithm();
        List<ServerConfig> serverList = loadBalancerConfiguration.getServers();

        switch (selectedAlgorithm) {
            case ROUND_ROBIN:
                logger.info("Creating Round Robin Load Balancer");
                return new RoundRobinLoadBalancer(serverList);
            case WEIGHTED_ROUND_ROBIN:
                logger.info("Creating Weighted Round Robin Load Balancer");
                return new WeightedRoundRobinLoadBalancer(serverList);
            case LEAST_CONNECTIONS:
                logger.info("Creating Least Connections Load Balancer");
                return new LeastConnectionsLoadBalancer(serverList);
            default:
                logger.error("Unknown load balancing algorithm: {}", selectedAlgorithm);
                throw new IllegalArgumentException("Unknown load balancing algorithm: " + selectedAlgorithm);
        }
    }
}

