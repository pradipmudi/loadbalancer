package com.loadbalancer.controller;

import com.loadbalancer.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
public class RequestController {

    private final LoadBalancerService loadBalancerService;

    @Autowired
    public RequestController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<String> routeRequest(HttpServletRequest request,
                                               @RequestBody(required = false) String requestBody) {
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

        // Call the rerouteRequest method from LoadBalancerService to reroute the request
        return loadBalancerService.rerouteRequest(HttpMethod.valueOf(method), fullUrl, requestEntity);
    }


}
