package es.uib.lotery;

import es.uib.lotery.packet.SorteosRequestPacket;

public class SellerSocket {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java LotteryClient <serverHost> <serverPort> <sellerID>");
            return;
        }
        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);

        Seller seller = new Seller(args[2]);

        if(seller.connectClient()){
            SorteosRequestPacket sorteos = new SorteosRequestPacket();
            seller.requestSorteos(sorteos);
            seller.disconnectClient();
        }

        if(seller.connectServer(serverHost, serverPort)){
            System.out.println("Seller established");
            seller.listen();
        }




    }

}
