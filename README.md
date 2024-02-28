# Description

This project is a lightweight load balancer designed to simulate request redirection based on customizable configurations. Following algorithms have been used here for load balancing:

- Round Robin
- Weighted Round Robin
- Least Connections

I discuss when to use which one, in this blog, please give it a read for better clarity: https://medium.com/@pradipmudi24/dissecting-load-balancing-algorithms-lets-explore-the-what-s-why-s-and-when-s-b6fc9312aea9

# How to test the load balancer
### Prerequisites:
- For backend server testing, create a project with multiple endpoints or use an existing project. The technology stack of the project doesn't matter.
- Configure Run configurations of the project for multiple ports to run the project simultaneously on different ports.
### Configuration:
- Open `application.yml` of loadbalancer project
- Customize load balancing algorithms and backend server details under the lb section.
- Add the host and ports information for each backend server, allowing flexibility to specify different hosts and ports.
### Testing:
- Start backend servers by running the project on configured ports.
- Configure and start the load balancer server
- Send test requests to the load balancer.
- Verify that requests are distributed among backend servers based on the configured algorithm (e.g., ROUND_ROBIN).
