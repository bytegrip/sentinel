package sh.myo.sentinel.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import sh.myo.sentinel.Tags;

public class PacketHandler {
    
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);
    
    private static int packetId = 0;
    
    public static void registerPackets() {
        INSTANCE.registerMessage(PacketRequestPlayerPositions.Handler.class, PacketRequestPlayerPositions.class, packetId++, Side.SERVER);
        INSTANCE.registerMessage(PacketPlayerPositionsResponse.Handler.class, PacketPlayerPositionsResponse.class, packetId++, Side.CLIENT);
    }
}
