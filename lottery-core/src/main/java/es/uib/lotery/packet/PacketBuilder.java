package es.uib.lotery.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketBuilder {
    public static final byte DNS_REQUEST_PACKET_ID = 0x00;
    public static final byte DNS_RESPONSE_PACKET_ID = 0x01;
    public static final byte DRAW_REQUEST_PACKET_ID = 0x02;
    public static final byte DRAW_RESPONSE_PACKET_ID = 0x03;
    public static final byte SELLER_REQUEST_PACKET_ID = 0x04;
    public static final byte SELLER_RESPONSE_PACKET_ID = 0x05;
    public static final byte SORTEOS_REQUEST_PACKET_ID = 0x06;
    public static final byte SORTEOS_RESPONSE_PACKET_ID = 0x07;
    public static final byte DNS_CONNECT_PACKET_ID = 0x08;
    public static final byte DNS_SERVERS_REQUEST_PACKET_ID = 0x09;
    public static final byte DNS_SERVERS_RESPONSE_PACKET_ID = 0x0A;


    public static final PacketBuilder INSTANCE = new PacketBuilder();

    private PacketBuilder() {}

    public BasePacket buildPacket(ByteBuffer byteBuffer) throws IOException {
        BasePacket packet = null;
        int id = PacketUtils.getByte(byteBuffer);

        // Check the packet depending on its ID
        switch (id) {
            case DNS_REQUEST_PACKET_ID -> packet = new DNSRequestPacket();
            case DNS_RESPONSE_PACKET_ID -> packet = new DNSResponsePacket();
            case DRAW_REQUEST_PACKET_ID -> packet = new DrawRequestPacket();
            case DRAW_RESPONSE_PACKET_ID -> packet = new DrawResponsePacket();
            case SELLER_REQUEST_PACKET_ID -> packet = new SellerRequestPacket();
            case SELLER_RESPONSE_PACKET_ID -> packet = new SellerResponsePacket();
            case SORTEOS_REQUEST_PACKET_ID -> packet = new SorteosRequestPacket();
            case SORTEOS_RESPONSE_PACKET_ID -> packet = new SorteosResponsePacket();
            case DNS_CONNECT_PACKET_ID -> packet = new DNSConnectPacket();
            case DNS_SERVERS_REQUEST_PACKET_ID -> packet = new DNSServersRequestPacket();
            case DNS_SERVERS_RESPONSE_PACKET_ID -> packet = new DNSServersResponsePacket();
        }

        // If no matching id for packet return null
        if (packet != null) packet = packet.decode(byteBuffer);
        return packet;
    }
}
