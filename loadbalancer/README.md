# Description

This project is a lightweight load balancer designed to simulate request redirection based on customizable configurations. Following algorithms have been used here for load balancing:

- Round Robin
- Weighted Round Robin
- Least Connections

I discuss when to use which one, in this blog, please give it a read for better clarity: https://medium.com/@pradipmudi24/dissecting-load-balancing-algorithms-lets-explore-the-what-s-why-s-and-when-s-b6fc9312aea9

# How to test it
- Create a small project with multiple endpoints or use an existing project, tech-stack of the project doesn't matter
- Configure Run configurations of the project for multiple ports
- Start all the run configurations of the project
- Jump to the loadbalancer `application.yml` file and configure the host and port details under server label
- Set the specific loadbalancing algorithm `enabled` property as `true`
- Shoot up the loadbalancer server in your local
- You are all set to test the load balancer server
