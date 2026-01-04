package sh.myo.sentinel.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sh.myo.sentinel.gui.GuiDimensionScanner;

import java.util.HashMap;
import java.util.Map;

public class PacketDimensionDataResponse implements IMessage {

    private Map<Integer, String> dimensionData;

    public PacketDimensionDataResponse() {
        this.dimensionData = new HashMap<>();
    }

    public PacketDimensionDataResponse(Map<Integer, String> dimensionData) {
        this.dimensionData = dimensionData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        dimensionData = new HashMap<>();
        
        for (int i = 0; i < size; i++) {
            int dimId = buf.readInt();
            String status = ByteBufUtils.readUTF8String(buf);
            dimensionData.put(dimId, status);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionData.size());
        
        for (Map.Entry<Integer, String> entry : dimensionData.entrySet()) {
            buf.writeInt(entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }
    }

    public static class Handler implements IMessageHandler<PacketDimensionDataResponse, IMessage> {
        @Override
        public IMessage onMessage(PacketDimensionDataResponse message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiDimensionScanner) {
                    GuiDimensionScanner gui = (GuiDimensionScanner) Minecraft.getMinecraft().currentScreen;
                    gui.setDimensionData(message.dimensionData);
                }
            });
            
            return null;
        }
    }
}
