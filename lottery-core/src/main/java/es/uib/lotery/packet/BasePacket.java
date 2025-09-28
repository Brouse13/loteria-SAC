package es.uib.lotery.packet;

import java.nio.ByteBuffer;

public interface BasePacket {
    int getId();

    byte[] encode(ByteBuffer buffer);

    BasePacket decode(ByteBuffer buffer);
}
