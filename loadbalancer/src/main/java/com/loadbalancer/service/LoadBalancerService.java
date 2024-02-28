package com.loadbalancer.service;

import com.loadbalancer.algorithms.LeastConnectionsLoadBalancer;
import com.loadbalancer.algorithms.LoadBalancer;
import com.loadbalancer.config.ServerConfig;
import com.loadbalancer.factory.LoadBalancerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LoadBalancerService {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerService.class);

    private final RestTemplate restTemplate;
    private final LoadBalancerFactory loadBalancerFactory;

    @Autowired
    public LoadBalancerService( RestTemplate restTemplate, LoadBalancerFactory loadBalancerFactory) {
        this.loadBalancerFactory = loadBalancerFactory;
        this.restTemplate = restTemplate;
    }

    // Method to reroute incoming requests to servers using the selected load balancing algorithm
    public ResponseEntity<String> rerouteRequest(HttpMethod method, String requestUrl, HttpEntity<String> requestEntity) {
        LoadBalancer loadBalancer = loadBalancerFactory.getConfiguredLoadBalancingAlgorithm();
        ServerConfig server = loadBalancer.getNextEligibleServer();

        if (loadBalancer instanceof LeastConnectionsLoadBalancer leastConnectionsLoadBalancer) {
            logger.debug("Incrementing connections for server: {}", server);
            leastConnectionsLoadBalancer.incrementConnections(server);
        }

        // Construct the URL for the selected server
        String serverUrl = server.getUrl() + requestUrl;

        logger.info("Forwarding request to server: {}", serverUrl);

        // Forward the request to the selected server
        ResponseEntity<String> responseEntity = restTemplate.exchange(serverUrl, method, requestEntity, String.class);

        if (loadBalancer instanceof LeastConnectionsLoadBalancer leastConnectionsLoadBalancer) {
            logger.debug("Decrementing connections for server: {}", server);
            leastConnectionsLoadBalancer.decrementConnections(server);
        }

        return responseEntity;
    }

    public ResponseEntity<String> forwardRequest(HttpServletRequest request, String requestBody) {
        try {
            String method = request.getMethod();

            // Extract path and query parameters separately
            String requestUri = request.getRequestURI();
            String queryString = request.getQueryString();

            // Construct the URL with only path parameters
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(requestUri);

            // Append query parameters if they exist
            if (queryString != null) {
                uriBuilder.query(queryString);
            }

            String fullUrl = uriBuilder.toUriString();

            // Construct HttpEntity if requestBody is not null
            HttpEntity<String> requestEntity = requestBody != null ? new HttpEntity<>(requestBody) : null;

            // Call rerouteRequest method and return the response
            return rerouteRequest(HttpMethod.valueOf(method), fullUrl, requestEntity);
        } catch (HttpClientErrorException e) {
            // Handle HttpClientErrorException
            // For example, return ResponseEntity with the appropriate status code and error message
            return ResponseEntity.status(e.getStatusCode()).body("{ HTTP Method: "+request.getMethod()+", HTTP Status - "+e.getMessage()+" } ");
        } catch (Exception e) {
            // Handle other exceptions
            // For example, return ResponseEntity with internal server error status code and a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}
