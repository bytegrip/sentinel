package sh.myo.sentinel.item;

public class ItemIronRadar extends ItemTieredRadar {
    public ItemIronRadar() {
        super("Iron", 32, 20000, 43.0, "Iron Blocks", 8);
        setRegistryName("iron_radar");
        setTranslationKey("sentinel.iron_radar");
    }
}
