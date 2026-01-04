package sh.myo.sentinel.item;

public class ItemStoneRadar extends ItemTieredRadar {
    public ItemStoneRadar() {
        super("Stone", 32, 2000, 54.0, "Cobblestone", 32);
        setRegistryName("stone_radar");
        setTranslationKey("sentinel.stone_radar");
    }
}
