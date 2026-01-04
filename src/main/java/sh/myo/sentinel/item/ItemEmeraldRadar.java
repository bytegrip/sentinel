package sh.myo.sentinel.item;

public class ItemEmeraldRadar extends ItemTieredRadar {
    public ItemEmeraldRadar() {
        super("Emerald", 32, 6000000, 21.0, "Emerald Blocks", 3);
        setRegistryName("emerald_radar");
        setTranslationKey("sentinel.emerald_radar");
    }
}
