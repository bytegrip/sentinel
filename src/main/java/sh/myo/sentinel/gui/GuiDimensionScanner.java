package sh.myo.sentinel.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import sh.myo.sentinel.item.ItemDimensionScanner;
import sh.myo.sentinel.network.PacketHandler;
import sh.myo.sentinel.network.PacketRequestDimensionData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuiDimensionScanner extends GuiScreen {

    private static final int BUTTON_SCAN = 0;
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 200;
    
    private ItemStack scannerStack = ItemStack.EMPTY;
    private int scannerFuel = 0;
    private Map<Integer, String> dimensionData = new HashMap<>();
    private boolean isScanning = false;
    private long scanStartTime = 0;
    private static final long SCAN_DURATION = 2000;
    
    private int scrollOffset = 0;
    private static final int MAX_VISIBLE_DIMENSIONS = 8;
    
    public GuiDimensionScanner(ItemStack scannerStack) {
        this.scannerStack = scannerStack;
        this.scannerFuel = ItemDimensionScanner.getFuel(scannerStack);
    }

    @Override
    public void initGui() {
        super.initGui();
        
        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        
        int buttonX = guiLeft + 10;
        int buttonY = guiTop + GUI_HEIGHT - 30;
        int buttonWidth = GUI_WIDTH - 20;
        this.buttonList.add(new GuiButton(BUTTON_SCAN, buttonX, buttonY, buttonWidth, 20, "SCAN DIMENSIONS"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == BUTTON_SCAN && !isScanning) {
            if (scannerFuel >= 8 && !scannerStack.isEmpty()) {
                consumeScannerFuel();
                startScan();
                PacketHandler.INSTANCE.sendToServer(new PacketRequestDimensionData());
            }
        }
    }

    private void startScan() {
        isScanning = true;
        scanStartTime = System.currentTimeMillis();
        dimensionData.clear();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        
        if (isScanning && System.currentTimeMillis() - scanStartTime > SCAN_DURATION) {
            isScanning = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        
        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        
        drawRect(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0xCC000000);
        drawRect(guiLeft + 2, guiTop + 2, guiLeft + GUI_WIDTH - 2, guiTop + GUI_HEIGHT - 2, 0xFF1A1A1A);
        
        String title = "Dimension Scanner";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, guiLeft + (GUI_WIDTH - titleWidth) / 2, guiTop + 10, 0x00FFFF);
        
        String fuelText = "Fuel: " + scannerFuel + "/8 Thulium Blocks";
        int fuelColor = scannerFuel >= 8 ? 0xFFFFFF : 0xFF0000;
        this.fontRenderer.drawString(fuelText, guiLeft + 10, guiTop + 25, fuelColor);
        
        int listTop = guiTop + 40;
        int listHeight = GUI_HEIGHT - 80;
        
        if (isScanning) {
            String scanText = "SCANNING...";
            int scanWidth = this.fontRenderer.getStringWidth(scanText);
            this.fontRenderer.drawString(scanText, guiLeft + (GUI_WIDTH - scanWidth) / 2, listTop + listHeight / 2, 0x00FFFF);
        } else if (dimensionData.isEmpty()) {
            String emptyText = "No scan data available";
            int emptyWidth = this.fontRenderer.getStringWidth(emptyText);
            this.fontRenderer.drawString(emptyText, guiLeft + (GUI_WIDTH - emptyWidth) / 2, listTop + listHeight / 2, 0x888888);
        } else {
            drawDimensionList(guiLeft, listTop, listHeight);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawDimensionList(int guiLeft, int listTop, int listHeight) {
        int yOffset = 0;
        int index = 0;
        
        for (Map.Entry<Integer, String> entry : dimensionData.entrySet()) {
            if (index < scrollOffset) {
                index++;
                continue;
            }
            
            if (yOffset >= listHeight - 20) {
                break;
            }
            
            int dimId = entry.getKey();
            String status = entry.getValue();
            
            int entryY = listTop + yOffset;
            drawRect(guiLeft + 5, entryY, guiLeft + GUI_WIDTH - 5, entryY + 18, 0xFF2A2A2A);
            
            String dimName = getDimensionName(dimId);
            this.fontRenderer.drawString(dimName, guiLeft + 10, entryY + 5, 0xFFFFFF);
            
            int statusColor = status.equals("Inhabited") ? 0xFF5555 : 0x55FF55;
            int statusWidth = this.fontRenderer.getStringWidth(status);
            this.fontRenderer.drawString(status, guiLeft + GUI_WIDTH - statusWidth - 10, entryY + 5, statusColor);
            
            yOffset += 20;
            index++;
        }
    }

    private String getDimensionName(int dimId) {
        switch (dimId) {
            case -1: return "The Nether";
            case 0: return "Overworld";
            case 1: return "The End";
            default: return "Dimension " + dimId;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        int mouseWheel = Mouse.getEventDWheel();
        if (mouseWheel != 0) {
            if (mouseWheel > 0) {
                scrollOffset = Math.max(0, scrollOffset - 1);
            } else {
                int maxScroll = Math.max(0, dimensionData.size() - MAX_VISIBLE_DIMENSIONS);
                scrollOffset = Math.min(maxScroll, scrollOffset + 1);
            }
        }
    }

    public void setDimensionData(Map<Integer, String> data) {
        this.dimensionData = data;
    }

    private void consumeScannerFuel() {
        if (!scannerStack.isEmpty()) {
            ItemDimensionScanner.consumeFuel(scannerStack);
            scannerFuel = ItemDimensionScanner.getFuel(scannerStack);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
