package com.loadbalancer.config;

import com.loadbalancer.constant.LoadBalancingAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "lb")
public class LoadBalancerConfiguration {
    private List<AlgorithmConfig> algorithms;

    public List<AlgorithmConfig> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<AlgorithmConfig> algorithms) {
        this.algorithms = algorithms;
    }

    public LoadBalancingAlgorithm getSelectedAlgorithm() {
        for (AlgorithmConfig algorithmConfig : algorithms) {
            if (algorithmConfig.isEnabled()) {
                return algorithmConfig.getAlgorithm();
            }
        }
        return null; // Or throw an exception if none is selected
    }
}

