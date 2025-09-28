package es.uib.lotery.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketBuilder {
    public static final byte DNS_REQUEST_PACKET_ID = 0x00;
    public static final byte DNS_RESPONSE_PACKET_ID = 0x01;

    public static final PacketBuilder INSTANCE = new PacketBuilder();

    private PacketBuilder() {}

    public BasePacket buildPacket(ByteBuffer byteBuffer) throws IOException {
        BasePacket packet = null;

        // Check the packet depending on its ID
        switch (PacketUtils.readByte(byteBuffer)) {
            case DNS_REQUEST_PACKET_ID -> packet = new DNSRequestPacket();
            case DNS_RESPONSE_PACKET_ID -> packet = new DNSResponsePacket();
            case MOVE_EVENT_PACKET -> packet = new MoveEventPacket();
            case SERVER_RESPONSE_PACKET -> packet = new ServerResponsePacket();
        }

        // If no matching id for packet return null
        if (packet != null) packet.decode(byteBuffer);
        return packet;
    }
}
