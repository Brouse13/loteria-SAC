package es.uib.lotery.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketBuilder {
    public static final byte DNS_REQUEST_PACKET_ID = 0x00;
    public static final byte DNS_RESPONSE_PACKET_ID = 0x01;
    public static final byte DRAW_REQUEST_PACKET_ID = 0x02;
    public static final byte DRAW_RESPONSE_PACKET_ID = 0x03;


    public static final PacketBuilder INSTANCE = new PacketBuilder();

    private PacketBuilder() {}

    public BasePacket buildPacket(ByteBuffer byteBuffer) throws IOException {
        BasePacket packet = null;

        // Check the packet depending on its ID
        switch (PacketUtils.getByte(byteBuffer)) {
            case DNS_REQUEST_PACKET_ID -> packet = new DNSRequestPacket();
            case DNS_RESPONSE_PACKET_ID -> packet = new DNSResponsePacket();
            case DRAW_REQUEST_PACKET_ID -> packet = new DrawRequestPacket();
            case DRAW_RESPONSE_PACKET_ID -> packet = new DrawResponsePacket();
        }

        // If no matching id for packet return null
        if (packet != null) packet.decode(byteBuffer);
        return packet;
    }
}
