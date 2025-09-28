package es.uib.lotery.packet;

import java.nio.ByteBuffer;

public interface BasePacket {
    byte getId();

    void encode(ByteBuffer buffer);

    BasePacket decode(ByteBuffer buffer);
}
