package sh.myo.sentinel.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class PacketRequestPlayerPositions implements IMessage {

    private static final double MAX_RADIUS = 500.0;
    private static final double STRONG_SIGNAL_RADIUS = 200.0;

    public PacketRequestPlayerPositions() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketRequestPlayerPositions, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestPlayerPositions message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.getServerWorld().addScheduledTask(() -> {
                Map<String, Integer> sectorSignals = new HashMap<>();
                
                for (EntityPlayerMP targetPlayer : player.getServerWorld().getMinecraftServer().getPlayerList().getPlayers()) {
                    if (targetPlayer == player || targetPlayer.dimension != player.dimension) {
                        continue;
                    }
                    
                    double dx = targetPlayer.posX - player.posX;
                    double dz = targetPlayer.posZ - player.posZ;
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    
                    if (distance > MAX_RADIUS) {
                        continue;
                    }
                    
                    double worldAngle = Math.toDegrees(Math.atan2(dx, -dz));
                    if (worldAngle < 0) worldAngle += 360;
                    
                    String playerName = targetPlayer.getName() + "|" + worldAngle + "|" + distance;
                    
                    int strength = distance <= STRONG_SIGNAL_RADIUS ? 
                        7 - (int)((distance / STRONG_SIGNAL_RADIUS) * 4) : 
                        3 - (int)(((distance - STRONG_SIGNAL_RADIUS) / (MAX_RADIUS - STRONG_SIGNAL_RADIUS)) * 2);
                    
                    strength = Math.max(1, Math.min(7, strength)); 
                    
                    sectorSignals.put(playerName, strength);
                }
                
                PacketHandler.INSTANCE.sendTo(new PacketPlayerPositionsResponse(sectorSignals), player);
            });
            
            return null;
        }
    }
}
