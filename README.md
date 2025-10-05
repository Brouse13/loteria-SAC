# Lottery-SAC

Created by [Brouse13](https://github.com/Brouse13) and [lucassabater](https://github.com/lucassabater)

## 📖 Project Description

Lottery is a distributed system based on several types of servers that interact to manage the lottery in real time.  
The project is organized into multiple Maven modules, generating independent executable JARs for each server type.

Available server types:

| Server   | Description                                                                         |
|----------|-------------------------------------------------------------------------------------|
| `dns`    | DNS server, responsible for resolving and distributing seller addresses to clients. |
| `server` | Main server that sells tickets and conducts the draws.                              |
| `seller` | Ticket sales server.                                                                |
| `client` | Client that queries and participates in the lottery.                                |

---

## 📖 Project Description

Each client can add a file called `configuration.yaml` that stores all the common project configuration. This file has to
be on the same dir that the .jar, and if no provided it will use the default:

````yaml
dnsHost: "192.168.1.174"    # DNS host
dnsPort: 8080               # DNS port
serverHost: "192.168.1.174" # Server host
serverPort: 3030            # Server port
retryInSeconds: 3           # Time between lottery request from the client to the server
randomSize: 100             # Size of the numbers on the raffle [0 - n]
````

---

## 📌 How to Run the Project

To ensure the system works correctly, servers must be started in the following order:

1. **Main Server (`server`) and DNS (`dns`)**
    - One instance of each type in any order.
2. **Sellers (`seller`)**
    - As many instances as needed, each with a unique name.
3. **Clients (`client`)**
    - As many instances as needed.

---

## 🛠️ Build

### Compiling the Project

To compile the project, run the following command from the root directory:

```bash
  mvn clean package
```

This will generate the different JARs:
````
lottery-dns/target/lottery-dns-<version>.jar
lottery-server/target/lottery-server-<version>.jar
lottery-seller/target/lottery-seller-<version>.jar
lottery-client/target/lottery-client-<version>.jar
````

## 🐳 Generating the Docker Image

To build the Docker image from the root of the project:

````bash
  docker build -t lottery-app .
````

| SERVER_TYPE | JAVA_ARGS                                 |
|-------------|-------------------------------------------|
| dns         | no arguments required                     |
| server      | no arguments required                     |
| seller      | \<sellerHost> \<sellerPort> \<sellerName> |
| client      | no arguments required                     |

\* In JAVA_ARGS, besides the required arguments, you can also include JVM flags before the arguments, e.g., JAVA_ARGS="-Xmx512m ..."

## 📝 Versions
### **1.0.0**
- Created first working version of the project
### **1.0.1**
- Fixed DNS startup
### **1.1.0**
- Added logger
- Added configuration.yaml