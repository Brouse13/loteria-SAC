package es.uib.lottery;

public class DNS {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java LotteryClient <dnsHost> <dnsPort>");
            return;
        }

        String dnsHost = args[0];
        int dnsPort;
        try {
            dnsPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            dnsPort = 8080;
            System.out.println("Using default dns port: " + dnsPort);
        }
        new DNS().start(dnsHost, dnsPort);
    }

    private void start(String host, int port) {
        DNSSocket dnsSocket = new DNSSocket();

        try {
            dnsSocket.start(host, port);
        }catch (Exception e) {
            dnsSocket.stop();
        }
    }
}
