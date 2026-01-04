package sh.myo.sentinel.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sh.myo.sentinel.item.ItemSentinelJammer;
import sh.myo.sentinel.item.ItemTieredRadar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PacketRequestPlayerPositions implements IMessage {

    public PacketRequestPlayerPositions() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketRequestPlayerPositions, IMessage> {
        private static final Random random = new Random();
        
        @Override
        public IMessage onMessage(PacketRequestPlayerPositions message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            double maxRange = 10000.0;
            double anglePrecision = 45.0;
            
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            
            if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemTieredRadar) {
                ItemTieredRadar radar = (ItemTieredRadar) mainHand.getItem();
                maxRange = radar.getRange();
                anglePrecision = radar.getAnglePrecision();
            } else if (!offHand.isEmpty() && offHand.getItem() instanceof ItemTieredRadar) {
                ItemTieredRadar radar = (ItemTieredRadar) offHand.getItem();
                maxRange = radar.getRange();
                anglePrecision = radar.getAnglePrecision();
            }
            
            final double finalMaxRange = maxRange;
            final double finalAnglePrecision = anglePrecision;
            
            player.getServerWorld().addScheduledTask(() -> {
                Map<Integer, Integer> sectorCounts = new HashMap<>();
                
                for (EntityPlayerMP targetPlayer : Objects.requireNonNull(player.getServerWorld().getMinecraftServer()).getPlayerList().getPlayers()) {
                    if (targetPlayer == player || targetPlayer.dimension != player.dimension) {
                        continue;
                    }
                    
                    double dx = targetPlayer.posX - player.posX;
                    double dz = targetPlayer.posZ - player.posZ;
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    
                    if (distance > finalMaxRange) {
                        continue;
                    }
                    
                    boolean isJammed = ItemSentinelJammer.hasJammer(targetPlayer);
                    
                    if (!isJammed) {
                        double worldAngle = Math.toDegrees(Math.atan2(dx, -dz));
                        if (worldAngle < 0) worldAngle += 360;
                        
                        double angleOffset = (random.nextDouble() - 0.5) * finalAnglePrecision;
                        double impreciseAngle = worldAngle + angleOffset;
                        
                        while (impreciseAngle < 0) impreciseAngle += 360;
                        while (impreciseAngle >= 360) impreciseAngle -= 360;
                        
                        int sector = (int)(impreciseAngle / finalAnglePrecision);
                        
                        sectorCounts.put(sector, sectorCounts.getOrDefault(sector, 0) + 1);
                    }
                }
                
                Map<String, Integer> sectorSignals = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : sectorCounts.entrySet()) {
                    int sector = entry.getKey();
                    int count = entry.getValue();
                    
                    double sectorCenterAngle = (sector + 0.5) * finalAnglePrecision;
                    
                    String sectorData = "sector_" + sector + "|" + sectorCenterAngle + "|" + finalAnglePrecision;
                    sectorSignals.put(sectorData, count);
                }
                
                PacketHandler.INSTANCE.sendTo(new PacketPlayerPositionsResponse(sectorSignals), player);
            });
            
            return null;
        }
    }
}
