package sh.myo.sentinel.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import sh.myo.sentinel.network.PacketHandler;
import sh.myo.sentinel.network.PacketRequestPlayerPositions;

import java.io.IOException;
import java.util.*;

public class GuiSentinelRadar extends GuiScreen {

    private static final int BUTTON_LOOKUP = 0;
    private static final int RADAR_SIZE = 205;
    private static final int BUTTON_WIDTH = 100;
    private static final int PADDING = 20;
    
    private List<PlayerPosition> playerPositions = new ArrayList<>();
    private boolean isScanning = false;
    private long scanStartTime = 0;
    private static final long SCAN_DURATION = 5000;
    private Random random = new Random();

    private static class PlayerPosition {
        String name;
        double angle;
        double distance;
        double noiseX;
        double noiseY;
        
        PlayerPosition(String name, double angle, double distance) {
            this.name = name;
            this.angle = angle;
            this.distance = distance;
            this.noiseX = 0;
            this.noiseY = 0;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        
        int totalWidth = RADAR_SIZE + PADDING + BUTTON_WIDTH;
        int uiStartX = (this.width - totalWidth) / 2;
        int centerY = this.height / 2;
        
        int buttonX = uiStartX + RADAR_SIZE + PADDING;
        int buttonY = centerY - RADAR_SIZE / 2 + 40;
        this.buttonList.add(new GuiButton(BUTTON_LOOKUP, buttonX, buttonY, BUTTON_WIDTH, 20, "SCAN"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == BUTTON_LOOKUP && !isScanning) {
            startScan();
            PacketHandler.INSTANCE.sendToServer(new PacketRequestPlayerPositions());
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
        
        int totalWidth = RADAR_SIZE + PADDING + BUTTON_WIDTH;
        int uiStartX = (this.width - totalWidth) / 2;
        int centerY = this.height / 2;
        
        int radarCenterX = uiStartX + RADAR_SIZE / 2;
        
        String title = "SENTINEL RADAR";
        int titleX = uiStartX + RADAR_SIZE + PADDING;
        int titleY = centerY - RADAR_SIZE / 2 + 10;
        this.fontRenderer.drawString(title, titleX, titleY, 0x00FF00);
        
        drawRadar(radarCenterX, centerY, partialTicks);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawRadar(int centerX, int centerY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        
        float radius = RADAR_SIZE / 2.0f;
        
        drawCircle(centerX, centerY, radius, 0xFF0a0a0a);
        
        for (int i = 1; i <= 4; i++) {
            float r = radius * i / 4.0f;
            drawCircleOutline(centerX, centerY, r, 0x40404040);
        }
        
        drawLine(centerX - radius, centerY, centerX + radius, centerY, 0x40404040);
        drawLine(centerX, centerY - radius, centerX, centerY + radius, 0x40404040);
        
        GlStateManager.enableTexture2D();
        int compassColor = 0x00FF00;
        this.fontRenderer.drawString("N", centerX - 3, (int)(centerY - radius - 15), compassColor);
        this.fontRenderer.drawString("S", centerX - 3, (int)(centerY + radius + 5), compassColor);
        this.fontRenderer.drawString("E", (int)(centerX + radius + 5), centerY - 4, compassColor);
        this.fontRenderer.drawString("W", (int)(centerX - radius - 12), centerY - 4, compassColor);
        GlStateManager.disableTexture2D();
        
        if (isScanning) {
            long elapsed = System.currentTimeMillis() - scanStartTime;
            float progress = Math.min(1.0f, elapsed / (float) SCAN_DURATION);
            
            drawScanEffect(centerX, centerY, radius, progress);
            
            if (elapsed >= SCAN_DURATION) {
                isScanning = false;
            }
        }
        
        if (!playerPositions.isEmpty()) {
            drawPlayerSignals(centerX, centerY, radius);
        }
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glShadeModel(GL11.GL_FLAT);
        
        GlStateManager.popMatrix();
    }

    private void drawScanEffect(int centerX, int centerY, float radius, float progress) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        
        int centerColor = getColorForDistance(0.0f);
        GL11.glColor4f(
            ((centerColor >> 16) & 0xFF) / 255.0f,
            ((centerColor >> 8) & 0xFF) / 255.0f,
            (centerColor & 0xFF) / 255.0f,
            0.3f * progress
        );
        GL11.glVertex2f(centerX, centerY);
        
        int segments = 64;
        float maxRadius = radius * progress;
        
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float currentRadius = maxRadius + (float) (Math.sin(System.currentTimeMillis() * 0.001 + angle * 3) * 3);
            currentRadius = Math.min(currentRadius, radius);
            
            float x = centerX + currentRadius * (float) Math.cos(angle);
            float y = centerY + currentRadius * (float) Math.sin(angle);
            
            int edgeColor = getColorForDistance(currentRadius / radius);
            GL11.glColor4f(
                ((edgeColor >> 16) & 0xFF) / 255.0f,
                ((edgeColor >> 8) & 0xFF) / 255.0f,
                (edgeColor & 0xFF) / 255.0f,
                0.2f * progress
            );
            GL11.glVertex2f(x, y);
        }
        
        GL11.glEnd();
    }

    private void drawPlayerSignals(int centerX, int centerY, float radius) {
        if (playerPositions.isEmpty()) return;
        
        long currentTime = System.currentTimeMillis();
        int segments = 1440;
        float minInset = radius * 0.05f;
        float maxInset = radius * 0.4f;
        

        float[] density = new float[segments];
        
        for (int i = 0; i < segments; i++) {
            double angle = Math.toRadians(i * 360.0 / segments);
            float playerInfluence = 0;
            
            for (PlayerPosition player : playerPositions) {
                double angleDiff = Math.abs(angle - player.angle);
                if (angleDiff > Math.PI) {
                    angleDiff = 2 * Math.PI - angleDiff;
                }
                
                double influence = Math.exp(-angleDiff * angleDiff / 0.5);
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
        
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        
        for (int i = 0; i <= segments; i++) {
            int idx = i % segments;
            double angle = Math.toRadians(i * 360.0 / segments);
            
            float normalizedDensity = maxDensity > 0 ? smoothed[idx] / maxDensity : 0;
            
            float wave = (float) Math.sin(angle * 3 + currentTime * 0.002) * 1.5f;
            float inset = minInset + (maxInset - minInset) * normalizedDensity + wave;
            
            float outerX = centerX + radius * (float) Math.cos(angle);
            float outerY = centerY + radius * (float) Math.sin(angle);
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            GL11.glVertex2f(outerX, outerY);
            
            float innerRadius = radius - inset;
            float innerX = centerX + innerRadius * (float) Math.cos(angle);
            float innerY = centerY + innerRadius * (float) Math.sin(angle);
            
            float t = normalizedDensity;
            t = t * t * (3.0f - 2.0f * t);
            float red = 1.0f;
            float green = 1.0f - t * 0.9f;
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
        playerPositions.clear();
        
        for (Map.Entry<String, Integer> entry : sectorSignals.entrySet()) {
            String data = entry.getKey();
            

            String[] parts = data.split("\\|");
            if (parts.length != 3) continue;
            
            String playerName = parts[0];
            double angleDegrees = Double.parseDouble(parts[1]);
            double distanceBlocks = Double.parseDouble(parts[2]);
            
            double angle = Math.toRadians(angleDegrees - 90);
            
            double distance = Math.min(1.0, distanceBlocks / 500.0);
            
            playerPositions.add(new PlayerPosition(playerName, angle, distance));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
