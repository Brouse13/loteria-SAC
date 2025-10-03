package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SorteosResponsePacket implements BasePacket {
    public final byte id = PacketBuilder.SORTEOS_RESPONSE_PACKET_ID;
    private List<Integer> sorteos;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);

        // Escribir el tamaño de la lista
        PacketUtils.putInt(buffer, sorteos.size());

        // Escribir cada sorteo
        for (Integer sorteo : sorteos) {
            PacketUtils.putInt(buffer, sorteo);
        }
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        // Leer cantidad
        int size = PacketUtils.getInt(buffer);

        // Leer todos los sorteos
        List<Integer> sorteos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            sorteos.add(PacketUtils.getInt(buffer));
        }

        // Usando patrón builder como pediste
        return SorteosResponsePacket.builder()
                .sorteos(sorteos)
                .build();
    }
}

