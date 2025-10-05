package es.uib.lotery.utils;

import lombok.*;

@Data
public class Configuration {
    private String dnsHost;
    private int dnsPort;
    private String serverHost;
    private int serverPort;
    private int retryInSeconds;
    private int randomSize;
}
