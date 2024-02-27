package com.loadbalancer.controller;

import com.loadbalancer.service.LoadBalancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private LoadBalancerService loadBalancerService;

    @Autowired
    public RequestController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @RequestMapping("/routeRequest")
    public ResponseEntity<String> routeRequest(HttpMethod method, String requestUrl, HttpEntity<String> requestEntity) {
        // Call the rerouteRequest method from LoadBalancerService to reroute the request
        return loadBalancerService.rerouteRequest(method, requestUrl, requestEntity);
    }
}

