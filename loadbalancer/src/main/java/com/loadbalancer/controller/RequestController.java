package com.loadbalancer.controller;

import com.loadbalancer.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // Call the forwardRequest method from LoadBalancerService to reroute the request
        return loadBalancerService.forwardRequest(request, requestBody);
    }


}
