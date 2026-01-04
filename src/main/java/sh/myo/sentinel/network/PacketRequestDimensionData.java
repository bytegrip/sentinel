package sh.myo.sentinel.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PacketRequestDimensionData implements IMessage {

    public PacketRequestDimensionData() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketRequestDimensionData, IMessage> {
        private static final Random random = new Random();
        private static final double FALSE_RESULT_CHANCE = 0.2;
        
        @Override
        public IMessage onMessage(PacketRequestDimensionData message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.getServerWorld().addScheduledTask(() -> {
                Map<Integer, String> dimensionData = new HashMap<>();
                
                Integer[] allDimensions = DimensionManager.getStaticDimensionIDs();
                
                for (int dimId : allDimensions) {
                    boolean hasPlayers = player.getServer().getPlayerList().getPlayers().stream()
                        .anyMatch(p -> p.dimension == dimId);
                    
                    boolean shouldInvert = random.nextDouble() < FALSE_RESULT_CHANCE;
                    if (shouldInvert) {
                        hasPlayers = !hasPlayers;
                    }
                    
                    dimensionData.put(dimId, hasPlayers ? "Inhabited" : "Uninhabited");
                }
                
                PacketHandler.INSTANCE.sendTo(new PacketDimensionDataResponse(dimensionData), player);
            });
            
            return null;
        }
    }
}
