package es.uib.lotery.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketBuilder {
    public static final byte DNS_REQUEST_PACKET_ID = 0x00;
    public static final byte DNS_RESPONSE_PACKET_ID = 0x01;
    public static final byte SORTEO_REQUEST_PACKET_ID = 0x02;
    public static final byte SORTEO_RESPONSE_PACKET_ID = 0x03;
    public static final byte DNS_CONNECT_PACKET_ID = 0x04;


    public static final PacketBuilder INSTANCE = new PacketBuilder();

    private PacketBuilder() {}

    public BasePacket buildPacket(ByteBuffer byteBuffer) throws IOException {
        BasePacket packet = null;
        int id = PacketUtils.getByte(byteBuffer);

        // Check the packet depending on its ID
        switch (id) {
            case DNS_REQUEST_PACKET_ID -> packet = new DNSRequestPacket();
            case DNS_RESPONSE_PACKET_ID -> packet = new DNSResponsePacket();
            case DNS_CONNECT_PACKET_ID -> packet = new DNSConnectPacket();
            case SORTEO_REQUEST_PACKET_ID -> packet = new SorteoRequestPacket();
            case SORTEO_RESPONSE_PACKET_ID -> packet = new SorteoResponsePacket();
        }

        // If no matching id for packet return null
        if (packet != null) packet = packet.decode(byteBuffer);
        return packet;
    }
}
