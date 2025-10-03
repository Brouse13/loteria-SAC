package es.uib.lottery;

public class DNS {
    public static void main(String[] args) {
        new DNS().start();
    }

    private void start() {
        DNSSocket dnsSocket = new DNSSocket();

        try {
            dnsSocket.start();
        }catch (Exception e) {
            dnsSocket.stop();
        }
    }
}
