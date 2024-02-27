package com.loadbalancer.config;

public class ServerConfig {
    private String name;
    private String url;
    private int weight;
    private int connections; // Track number of connections to this server

    public ServerConfig(String name, String url, int weight) {
        this.name = name;
        this.url = url;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getWeight() {
        return weight;
    }

    public int getConnections() {
        return connections;
    }

    public void incrementConnections() {
        connections++;
    }

    public void decrementConnections() {
        connections--;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", weight=" + weight +
                ", connections=" + connections +
                '}';
    }
}
