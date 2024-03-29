package com.loadbalancer.config;

import com.loadbalancer.constant.LoadBalancingAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "lb")
public class LoadBalancerConfiguration {
    private List<AlgorithmConfig> algorithms;
    private List<ServerConfig> servers;

    public List<AlgorithmConfig> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<AlgorithmConfig> algorithms) {
        this.algorithms = algorithms;
    }

    public List<ServerConfig> getServers() {
        return servers;
    }

    public void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }

    public LoadBalancingAlgorithm getSelectedAlgorithm() {
        for (AlgorithmConfig algorithmConfig : algorithms) {
            if (algorithmConfig.isEnabled()) {
                return algorithmConfig.getAlgorithm();
            }
        }
        return null; // Or throw an exception if none is selected
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

