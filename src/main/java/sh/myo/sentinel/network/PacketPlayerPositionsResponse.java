package sh.myo.sentinel.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sh.myo.sentinel.gui.GuiSentinelRadar;

import java.util.HashMap;
import java.util.Map;

public class PacketPlayerPositionsResponse implements IMessage {

    private Map<String, Integer> sectorSignals;

    public PacketPlayerPositionsResponse() {
        sectorSignals = new HashMap<>();
    }

    public PacketPlayerPositionsResponse(Map<String, Integer> sectorSignals) {
        this.sectorSignals = sectorSignals;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        sectorSignals = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String sector = ByteBufUtils.readUTF8String(buf);
            int strength = buf.readInt();
            sectorSignals.put(sector, strength);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(sectorSignals.size());
        for (Map.Entry<String, Integer> entry : sectorSignals.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    public static class Handler implements IMessageHandler<PacketPlayerPositionsResponse, IMessage> {
        @Override
        public IMessage onMessage(PacketPlayerPositionsResponse message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiSentinelRadar) {
                    GuiSentinelRadar gui = (GuiSentinelRadar) Minecraft.getMinecraft().currentScreen;
                    gui.updateSectorSignals(message.sectorSignals);
                }
            });
            return null;
        }
    }
}
