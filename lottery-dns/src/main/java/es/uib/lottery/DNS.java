package es.uib.lottery;

public class DNS {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java LotteryClient");
            return;
        }

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
