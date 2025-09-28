package es.uib.lotery.packet;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PacketHandleRegistry {

    private final Map<
            Class<? extends BasePacket>,
            Function<? extends BasePacket, List<BasePacket>>
            > packetHandlers = new HashMap<>();

    public <T extends BasePacket> void registerHandler(Class<T> packetType, Function<T, List<BasePacket>> handler) {
        packetHandlers.put(packetType, handler);
    }

    /**
     * Procesa un paquete utilizando el manejador registrado.
     *
     * @param packet paquete que se desea procesar
     * @param <T>    tipo genérico del paquete
     * @return lista de respuestas generadas por el manejador, o null si no hay un manejador registrado.
     */
    @SuppressWarnings("unchecked")
    public <T extends BasePacket> List<BasePacket> handlePacket(@NonNull T packet) {
        Function<T, List<BasePacket>> handler = (Function<T, List<BasePacket>>) packetHandlers.get(packet.getClass());

        // Procesa el paquete si se encuentra un manejador
        if (handler != null) return handler.apply(packet);

        return null;
    }
}
