package sh.myo.sentinel.item;

public class ItemCoalRadar extends ItemTieredRadar {
    public ItemCoalRadar() {
        super("Coal", 32, 5000, 49.0, "Coal Blocks", 32);
        setRegistryName("coal_radar");
        setTranslationKey("sentinel.coal_radar");
    }
}
