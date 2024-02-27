package com.loadbalancer.service;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LoadBalancerService {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerService.class);

    private final LoadBalancerConfiguration loadBalancerConfiguration;
    private final RestTemplate restTemplate;

    @Autowired
    public LoadBalancerService(LoadBalancerConfiguration loadBalancerConfiguration, RestTemplate restTemplate) {
        this.loadBalancerConfiguration = loadBalancerConfiguration;
        this.restTemplate = restTemplate;
    }

    public LoadBalancingAlgorithm getLoadBalancingAlgorithm() {
        return loadBalancerConfiguration.getSelectedAlgorithm();
    }

    private List<ServerConfig> getServers() {
        return loadBalancerConfiguration.getServers();
    }

    public LoadBalancer loadTheLoadBalancingAlgorithmByConfig() {
        LoadBalancingAlgorithm selectedAlgorithm = getLoadBalancingAlgorithm();
        List<ServerConfig> serverList = getServers();
        switch (selectedAlgorithm) {
            case ROUND_ROBIN:
                logger.info("Loading Round Robin Load Balancer");
                return new RoundRobinLoadBalancer(serverList);
            case WEIGHTED_ROUND_ROBIN:
                logger.info("Loading Weighted Round Robin Load Balancer");
                return new WeightedRoundRobinLoadBalancer(serverList);
            case LEAST_CONNECTIONS:
                logger.info("Loading Least Connections Load Balancer");
                return new LeastConnectionsLoadBalancer(serverList);
            default:
                logger.error("Unknown load balancing algorithm: {}", selectedAlgorithm);
                throw new IllegalArgumentException("Unknown load balancing algorithm: " + selectedAlgorithm);
        }
    }

    // Method to reroute incoming requests to servers using the selected load balancing algorithm
    public ResponseEntity<String> rerouteRequest(HttpMethod method, String requestUrl, HttpEntity<String> requestEntity) {
        LoadBalancer loadBalancer = loadTheLoadBalancingAlgorithmByConfig();
        ServerConfig server = loadBalancer.getNextEligibleServer();

        if (loadBalancer instanceof LeastConnectionsLoadBalancer) {
            logger.debug("Incrementing connections for server: {}", server);
            ((LeastConnectionsLoadBalancer) loadBalancer).incrementConnections(server);
        }

        // Construct the URL for the selected server
        String serverUrl = server.getUrl() + requestUrl;

        logger.info("Forwarding request to server: {}", serverUrl);

        // Forward the request to the selected server
        ResponseEntity<String> responseEntity = restTemplate.exchange(serverUrl, method, requestEntity, String.class);

        if (loadBalancer instanceof LeastConnectionsLoadBalancer) {
            logger.debug("Decrementing connections for server: {}", server);
            ((LeastConnectionsLoadBalancer) loadBalancer).decrementConnections(server);
        }

        return responseEntity;
    }
}
