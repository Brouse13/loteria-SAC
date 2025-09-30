package es.uib.lotery;

import es.uib.lotery.packet.SorteosRequestPacket;

public class SellerSocket {
    public static void main(String[] args) {
        if (args.length != 3) { return;}

        Seller seller = new Seller(args[0]);

        if(seller.connectClient()){
            SorteosRequestPacket sorteos = new SorteosRequestPacket();
            seller.requestSorteos(sorteos);
            seller.disconnectClient();
        }


        if(seller.connectServer(args[1], Integer.parseInt(args[2]))){
            System.out.println("Server established");
        };


    }

}
