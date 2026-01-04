package sh.myo.sentinel;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.myo.sentinel.gui.GuiHandler;
import sh.myo.sentinel.item.*;
import sh.myo.sentinel.block.BlockThuliumOre;
import sh.myo.sentinel.block.BlockThuliumBlock;
import sh.myo.sentinel.network.PacketHandler;
import sh.myo.sentinel.world.WorldGenThulium;
import sh.myo.sentinel.crafting.RecipeTieredRadarRefuel;
import sh.myo.sentinel.recipe.RecipeJammerRefuel;
import sh.myo.sentinel.recipe.RecipeScannerRefuel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Objects;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Sentinel {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.Instance(Tags.MOD_ID)
    public static Sentinel instance;

    public static final ItemSentinelJammer SENTINEL_JAMMER = new ItemSentinelJammer();
    
    public static final ItemWoodenRadar WOODEN_RADAR = new ItemWoodenRadar();
    public static final ItemStoneRadar STONE_RADAR = new ItemStoneRadar();
    public static final ItemCoalRadar COAL_RADAR = new ItemCoalRadar();
    public static final ItemIronRadar IRON_RADAR = new ItemIronRadar();
    public static final ItemGoldRadar GOLD_RADAR = new ItemGoldRadar();
    public static final ItemDiamondRadar DIAMOND_RADAR = new ItemDiamondRadar();
    public static final ItemObsidianRadar OBSIDIAN_RADAR = new ItemObsidianRadar();
    public static final ItemEmeraldRadar EMERALD_RADAR = new ItemEmeraldRadar();
    public static final ItemThuliumRadar THULIUM_RADAR = new ItemThuliumRadar();
    
    public static final ItemDimensionScanner DIMENSION_SCANNER = new ItemDimensionScanner();
    
    public static final BlockThuliumOre THULIUM_ORE_BLOCK = new BlockThuliumOre();
    public static final BlockThuliumBlock THULIUM_BLOCK = new BlockThuliumBlock();
    public static final ItemThuliumOre THULIUM_ORE_ITEM = new ItemThuliumOre();
    public static final ItemThuliumIngot THULIUM_INGOT = new ItemThuliumIngot();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        
        GameRegistry.registerWorldGenerator(new WorldGenThulium(), 0);
    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().register(THULIUM_ORE_BLOCK);
            event.getRegistry().register(THULIUM_BLOCK);
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(WOODEN_RADAR);
            event.getRegistry().register(STONE_RADAR);
            event.getRegistry().register(COAL_RADAR);
            event.getRegistry().register(IRON_RADAR);
            event.getRegistry().register(GOLD_RADAR);
            event.getRegistry().register(DIAMOND_RADAR);
            event.getRegistry().register(OBSIDIAN_RADAR);
            event.getRegistry().register(EMERALD_RADAR);
            event.getRegistry().register(THULIUM_RADAR);
            
            event.getRegistry().register(DIMENSION_SCANNER);
            event.getRegistry().register(SENTINEL_JAMMER);
            event.getRegistry().register(new ItemBlock(THULIUM_ORE_BLOCK).setRegistryName(Objects.requireNonNull(THULIUM_ORE_BLOCK.getRegistryName())));
            event.getRegistry().register(new ItemBlock(THULIUM_BLOCK).setRegistryName(Objects.requireNonNull(THULIUM_BLOCK.getRegistryName())));
            event.getRegistry().register(THULIUM_ORE_ITEM);
            event.getRegistry().register(THULIUM_INGOT);
        }
        
        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            event.getRegistry().register(new RecipeTieredRadarRefuel(WOODEN_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.LOG), 32)
                    .setRegistryName(Tags.MOD_ID, "wooden_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(STONE_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.COBBLESTONE), 32)
                    .setRegistryName(Tags.MOD_ID, "stone_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(COAL_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.COAL_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "coal_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(IRON_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.IRON_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "iron_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(GOLD_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.GOLD_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "gold_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(DIAMOND_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.DIAMOND_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "diamond_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(OBSIDIAN_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.OBSIDIAN), 32)
                    .setRegistryName(Tags.MOD_ID, "obsidian_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(EMERALD_RADAR, Item.getItemFromBlock(net.minecraft.init.Blocks.EMERALD_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "emerald_radar_refuel"));
            event.getRegistry().register(new RecipeTieredRadarRefuel(THULIUM_RADAR, Item.getItemFromBlock(THULIUM_BLOCK), 32)
                    .setRegistryName(Tags.MOD_ID, "thulium_radar_refuel"));
            
            event.getRegistry().register(new RecipeScannerRefuel().setRegistryName(Tags.MOD_ID, "scanner_refuel"));
            event.getRegistry().register(new RecipeJammerRefuel().setRegistryName(Tags.MOD_ID, "jammer_refuel"));
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            ModelLoader.setCustomModelResourceLocation(WOODEN_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(WOODEN_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(STONE_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(STONE_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(COAL_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(COAL_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(IRON_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(IRON_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(GOLD_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(GOLD_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(DIAMOND_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(DIAMOND_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(OBSIDIAN_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(OBSIDIAN_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(EMERALD_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(EMERALD_RADAR.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(THULIUM_RADAR, 0,
                    new ModelResourceLocation(Objects.requireNonNull(THULIUM_RADAR.getRegistryName()), "inventory"));
            
            ModelLoader.setCustomModelResourceLocation(DIMENSION_SCANNER, 0,
                    new ModelResourceLocation(Objects.requireNonNull(DIMENSION_SCANNER.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(SENTINEL_JAMMER, 0,
                    new ModelResourceLocation(Objects.requireNonNull(SENTINEL_JAMMER.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(THULIUM_ORE_BLOCK), 0,
                    new ModelResourceLocation(Objects.requireNonNull(THULIUM_ORE_BLOCK.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(THULIUM_BLOCK), 0,
                    new ModelResourceLocation(Objects.requireNonNull(THULIUM_BLOCK.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(THULIUM_ORE_ITEM, 0,
                    new ModelResourceLocation(Objects.requireNonNull(THULIUM_ORE_ITEM.getRegistryName()), "inventory"));
            ModelLoader.setCustomModelResourceLocation(THULIUM_INGOT, 0,
                    new ModelResourceLocation(Objects.requireNonNull(THULIUM_INGOT.getRegistryName()), "inventory"));
        }
    }
}
