package sh.myo.sentinel.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import sh.myo.sentinel.Sentinel;
import sh.myo.sentinel.network.PacketHandler;
import sh.myo.sentinel.item.ItemTieredRadar;
import sh.myo.sentinel.network.PacketHandler;
import sh.myo.sentinel.network.PacketRequestPlayerPositions;

import java.io.IOException;
import java.util.*;

public class GuiSentinelRadar extends GuiScreen {

    private static final int BUTTON_LOOKUP = 0;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 166;
    private static final int RADAR_SIZE = 150;
    private static final int RADAR_X = 8;
    private static final int RADAR_Y = 8;
    private static final int RIGHT_PANEL_X = 163;
    private static final int RIGHT_PANEL_Y = 7;
    private static final int RIGHT_PANEL_WIDTH = 85;
    private static final int RIGHT_PANEL_HEIGHT = 151;
    
    private List<PlayerPosition> playerPositions = new ArrayList<>();
    private List<PlayerPosition> pendingPlayerPositions = new ArrayList<>();
    private boolean isScanning = false;
    private long scanStartTime = 0;
    private static final long SCAN_DURATION = 3000;
    private Random random = new Random();
    private int radarFuel = 0;
    private int radarMaxFuel = 32;
    private int radarFuelPerUse = 32;
    private double radarRange = 1000;
    private double radarPrecision = 45.0;
    private ItemStack radarStack = ItemStack.EMPTY;
    
    public GuiSentinelRadar(ItemStack radarStack) {
        this.radarStack = radarStack;
        this.radarFuel = ItemTieredRadar.getFuel(radarStack);
        
        if (radarStack.getItem() instanceof ItemTieredRadar) {
            ItemTieredRadar radarItem = (ItemTieredRadar) radarStack.getItem();
            this.radarMaxFuel = radarItem.getMaxFuel();
            this.radarFuelPerUse = radarItem.getFuelPerUse();
            this.radarRange = radarItem.getRange();
            this.radarPrecision = radarItem.getAnglePrecision();
        }
    }

    private static class PlayerPosition {
        String name;
        double angle;
        double angleRange;
        double noiseX;
        double noiseY;
        
        PlayerPosition(String name, double angle, double angleRange) {
            this.name = name;
            this.angle = angle;
            this.angleRange = angleRange;
            this.noiseX = 0;
            this.noiseY = 0;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        
        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        
        int buttonX = guiLeft + RIGHT_PANEL_X + 5;
        int buttonY = guiTop + 138;
        int buttonWidth = (248 - 163) - 10;
        this.buttonList.add(new GuiButton(BUTTON_LOOKUP, buttonX, buttonY, buttonWidth, 20, "Scan"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == BUTTON_LOOKUP && !isScanning) {
            if (radarFuel > 0 && !radarStack.isEmpty()) {
                consumeRadarFuel();
                startScan();
                PacketHandler.INSTANCE.sendToServer(new PacketRequestPlayerPositions());
            }
        }
    }

    private void startScan() {
        isScanning = true;
        scanStartTime = System.currentTimeMillis();
        playerPositions.clear();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        
        drawRect(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0xCC000000);
        drawRect(guiLeft + 2, guiTop + 2, guiLeft + GUI_WIDTH - 2, guiTop + GUI_HEIGHT - 2, 0xFF1A1A1A);
        
        String title = "Sentinel Radar";
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, guiLeft + GUI_WIDTH - titleWidth - 10, guiTop + 10, 0x00FFFF);
        
        int radarCenterX = guiLeft + RADAR_X + RADAR_SIZE / 2;
        int radarCenterY = guiTop + RADAR_Y + RADAR_SIZE / 2;
        
        int fuelX = guiLeft + RIGHT_PANEL_X + 5;
        int fuelY = guiTop + 116; 
        drawThuliumDisplay(fuelX, fuelY);
        
        drawRadar(radarCenterX, radarCenterY, partialTicks);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawRadar(int centerX, int centerY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth(); 
        
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        
        float radius = RADAR_SIZE / 2.0f;
        
        if (!playerPositions.isEmpty() || (isScanning && !pendingPlayerPositions.isEmpty())) {
            drawPlayerSignals(centerX, centerY, radius);
        }
        
        drawCircle(centerX, centerY, radius, 0xFF0a0a0a);
        
        for (int i = 1; i <= 4; i++) {
            float r = radius * i / 4.0f;
            drawCircleOutline(centerX, centerY, r, 0x40404040);
        }
        
        drawLine(centerX - radius, centerY, centerX + radius, centerY, 0x40404040);
        drawLine(centerX, centerY - radius, centerX, centerY + radius, 0x40404040);
        
        if (isScanning) {
            long elapsed = System.currentTimeMillis() - scanStartTime;
            float progress = (elapsed / (float) SCAN_DURATION) * 1.2f - 0.1f;
            
            drawScanEffect(centerX, centerY, radius, progress);
            
            if (progress >= 1.1f) {
                isScanning = false;
                playerPositions.clear();
                playerPositions.addAll(pendingPlayerPositions);
                pendingPlayerPositions.clear();
            }
        }
        
        GlStateManager.enableTexture2D();
        int compassColor = 0xAAAAAA; 
        float compassInset = radius * 0.97f; 
        this.fontRenderer.drawString("N", centerX - 3, (int)(centerY - compassInset), compassColor);
        this.fontRenderer.drawString("S", centerX - 3, (int)(centerY + compassInset - 8), compassColor);
        this.fontRenderer.drawString("E", (int)(centerX + compassInset - 4), centerY - 4, compassColor);
        this.fontRenderer.drawString("W", (int)(centerX - compassInset), centerY - 4, compassColor);
        GlStateManager.disableTexture2D();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth(); 
        
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glShadeModel(GL11.GL_FLAT);
        
        GlStateManager.popMatrix();
    }

    private void drawScanEffect(int centerX, int centerY, float radius, float progress) {
        float fadeAlpha = 1.0f;
        if (progress < 0.0f) {
            fadeAlpha = 1.0f + (progress / 0.1f);
        } else if (progress > 1.0f) {
            fadeAlpha = 1.0f - ((progress - 1.0f) / 0.1f);
        }
        fadeAlpha = Math.max(0.0f, Math.min(1.0f, fadeAlpha));
        
        float sweepAngle = (float) (Math.max(0.0f, Math.min(1.0f, progress)) * Math.PI * 2);
        
        int trailSegments = 60;
        for (int i = 0; i < trailSegments; i++) {
            float trailProgress = i / (float) trailSegments;
            float trailAngle = sweepAngle - trailProgress * (float) Math.PI * 0.3f;
            float alpha = (1.0f - trailProgress * trailProgress) * fadeAlpha;
            
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4f(0.0f, 0.6f, 0.3f, alpha);
            GL11.glVertex2f(centerX, centerY);
            float endX = centerX + radius * (float) Math.cos(trailAngle);
            float endY = centerY + radius * (float) Math.sin(trailAngle);
            GL11.glVertex2f(endX, endY);
            GL11.glEnd();
        }
        
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(0.0f, 0.7f, 0.35f, fadeAlpha);
        GL11.glVertex2f(centerX, centerY);
        float mainEndX = centerX + radius * (float) Math.cos(sweepAngle);
        float mainEndY = centerY + radius * (float) Math.sin(sweepAngle);
        GL11.glVertex2f(mainEndX, mainEndY);
        GL11.glEnd();
        GL11.glLineWidth(1.0f);
    }

    private void drawPlayerSignals(int centerX, int centerY, float radius) {
        List<PlayerPosition> positionsToShow = isScanning ? pendingPlayerPositions : playerPositions;
        if (positionsToShow.isEmpty()) return;
        
        float scanProgress = 1.0f;
        if (isScanning) {
            long elapsed = System.currentTimeMillis() - scanStartTime;
            float extendedProgress = (elapsed / (float) SCAN_DURATION) * 1.2f - 0.1f;
            scanProgress = Math.max(0.0f, Math.min(1.0f, extendedProgress));
        }
        
        long currentTime = System.currentTimeMillis();
        int segments = 1440;
        float minInset = radius * 0.05f;
        float maxInset = radius * 0.4f;
        

        float[] density = new float[segments];
        
        for (int i = 0; i < segments; i++) {
            double angle = Math.toRadians(i * 360.0 / segments);
            float playerInfluence = 0;
            
            for (PlayerPosition player : positionsToShow) {
                double angleDiff = Math.abs(angle - player.angle);
                if (angleDiff > Math.PI) {
                    angleDiff = 2 * Math.PI - angleDiff;
                }
                
                double rangeRadians = Math.toRadians(player.angleRange);
                
                double spreadFactor = 1.5;
                double influence = Math.exp(-angleDiff * angleDiff / (rangeRadians * spreadFactor));
                playerInfluence += influence;
            }
            
            density[i] = playerInfluence;
        }
        
        float[] smoothed = new float[segments];
        float[] temp = density.clone();
        
        for (int pass = 0; pass < 5; pass++) {
            int smoothRadius = 25;
            for (int i = 0; i < segments; i++) {
                float sum = 0;
                float weight = 0;
                for (int j = -smoothRadius; j <= smoothRadius; j++) {
                    int idx = (i + j + segments) % segments;
                    float w = 1.0f - Math.abs(j) / (float) smoothRadius;
                    w = w * w;
                    sum += temp[idx] * w;
                    weight += w;
                }
                smoothed[i] = sum / weight;
            }
            temp = smoothed.clone();
        }
        
        float maxDensity = 0;
        for (float d : smoothed) {
            maxDensity = Math.max(maxDensity, d);
        }
        
        float sweepAngle = scanProgress * (float) Math.PI * 2;
        
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        
        for (int i = 0; i <= segments; i++) {
            int idx = i % segments;
            double angle = Math.toRadians(i * 360.0 / segments);
            
            if (isScanning) {
                double normalizedAngle = angle;
                while (normalizedAngle < 0) normalizedAngle += Math.PI * 2;
                double normalizedSweep = sweepAngle;
                while (normalizedSweep < 0) normalizedSweep += Math.PI * 2;
                
                if (normalizedAngle > normalizedSweep) {
                    continue;
                }
            }
            
            float normalizedDensity = maxDensity > 0 ? smoothed[idx] / maxDensity : 0;
            
            float wave = (float) Math.sin(angle * 3 + currentTime * 0.002) * 1.5f;
            float inset = minInset + (maxInset - minInset) * normalizedDensity + wave;
            
            float outerX = centerX + radius * (float) Math.cos(angle);
            float outerY = centerY + radius * (float) Math.sin(angle);
            
            float t = normalizedDensity;
            t = t * t * (3.0f - 2.0f * t);
            float red = t * 0.5f;
            float green = 0.6f - t * 0.15f;
            GL11.glColor4f(red, green, 0.0f, 1.0f);
            GL11.glVertex2f(outerX, outerY);
            
            float innerRadius = radius - inset;
            float innerX = centerX + innerRadius * (float) Math.cos(angle);
            float innerY = centerY + innerRadius * (float) Math.sin(angle);
            
            GL11.glColor4f(red, green, 0.0f, 1.0f);
            GL11.glVertex2f(innerX, innerY);
        }
        
        GL11.glEnd();
    }

    private int getColorForDistance(float distance) {
        float red = 1.0f;
        float green = distance;
        float blue = 0.0f;
        
        return ((int)(red * 255) << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
    }

    private void drawCircle(int centerX, int centerY, float radius, int color) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        
        GL11.glColor4f(r, g, b, a);
        GL11.glVertex2f(centerX, centerY);
        
        int segments = 32;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = centerX + radius * (float) Math.cos(angle);
            float y = centerY + radius * (float) Math.sin(angle);
            GL11.glVertex2f(x, y);
        }
        
        GL11.glEnd();
    }

    private void drawCircleOutline(int centerX, int centerY, float radius, int color) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        
        GL11.glColor4f(r, g, b, a);
        
        int segments = 64;
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = centerX + radius * (float) Math.cos(angle);
            float y = centerY + radius * (float) Math.sin(angle);
            GL11.glVertex2f(x, y);
        }
        
        GL11.glEnd();
    }

    private void drawLine(float x1, float y1, float x2, float y2, int color) {
        GL11.glBegin(GL11.GL_LINES);
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        
        GL11.glColor4f(r, g, b, a);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        
        GL11.glEnd();
    }

    public void updateSectorSignals(Map<String, Integer> sectorSignals) {
        pendingPlayerPositions.clear();
        
        for (Map.Entry<String, Integer> entry : sectorSignals.entrySet()) {
            String data = entry.getKey();
            

            String[] parts = data.split("\\|");
            if (parts.length != 3) continue;
            
            String playerName = parts[0];
            double centerAngleDegrees = Double.parseDouble(parts[1]);
            double angleRange = Double.parseDouble(parts[2]);
            
            double angle = Math.toRadians(centerAngleDegrees - 90);
            
            pendingPlayerPositions.add(new PlayerPosition(playerName, angle, angleRange));
        }
    }
    
    private void drawThuliumDisplay(int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        
        ItemStack fuelStack = ItemStack.EMPTY;
        if (radarStack.getItem() instanceof ItemTieredRadar) {
            fuelStack = ((ItemTieredRadar) radarStack.getItem()).getFuelDisplayItem();
        }
        if (fuelStack.isEmpty()) {
            fuelStack = new ItemStack(Sentinel.THULIUM_INGOT);
        }
        
        this.itemRender.renderItemAndEffectIntoGUI(fuelStack, x, y);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.4f, 1.4f, 1.0f);
        int color = radarFuel >= radarFuelPerUse ? 0xFFFFFF : 0xFF0000;
        String fuelText = radarFuel + "/" + radarFuelPerUse;
        this.fontRenderer.drawString(fuelText, (int)((x + 19) / 1.4f), (int)((y + 3) / 1.4f), color);
        GlStateManager.popMatrix();
    }
    
    private void consumeRadarFuel() {
        if (!radarStack.isEmpty()) {
            ItemTieredRadar.consumeFuel(radarStack, radarMaxFuel);
            radarFuel = ItemTieredRadar.getFuel(radarStack);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
