package com.loadbalancer.controller;

import com.loadbalancer.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

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
        String requestUrl = extractRequestUrl(request);

        // Extract path variables from the request URL
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Extract query parameters from the request URL
        Map<String, String[]> queryParams = request.getParameterMap();

        // Prepare the full URL with path variables and query parameters
        StringBuilder fullUrlBuilder = new StringBuilder(requestUrl);
        if (!pathVariables.isEmpty()) {
            for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                fullUrlBuilder.replace(fullUrlBuilder.indexOf("{" + entry.getKey() + "}"), fullUrlBuilder.indexOf("{" + entry.getKey() + "}") + entry.getKey().length() + 2, entry.getValue());
            }
        }
        if (!queryParams.isEmpty()) {
            fullUrlBuilder.append("?");
            for (Map.Entry<String, String[]> entry : queryParams.entrySet()) {
                for (String value : entry.getValue()) {
                    fullUrlBuilder.append(entry.getKey()).append("=").append(value).append("&");
                }
            }
            fullUrlBuilder.deleteCharAt(fullUrlBuilder.length() - 1);
        }
        String fullUrl = fullUrlBuilder.toString();

        // Construct HttpEntity if requestBody is not null
        HttpEntity<String> requestEntity = requestBody != null ? new HttpEntity<>(requestBody) : null;

        // Call the rerouteRequest method from LoadBalancerService to reroute the request
        return loadBalancerService.rerouteRequest(HttpMethod.valueOf(method), fullUrl, requestEntity);
    }

    private String extractRequestUrl(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return requestURI.substring(contextPath.length() + servletPath.length());
    }
}
