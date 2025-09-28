package es.uib.lotery.packet;

public interface BasePacket {
    int getId();

    byte[] encode(BasePacket buffer);

    BasePacket decode(byte[] bytes);
}
