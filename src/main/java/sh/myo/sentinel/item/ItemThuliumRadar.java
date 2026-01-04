package sh.myo.sentinel.item;

public class ItemThuliumRadar extends ItemTieredRadar {
    public ItemThuliumRadar() {
        super("Thulium", 32, 6000000, 15.0, "Thulium Blocks", 4);
        setRegistryName("thulium_radar");
        setTranslationKey("sentinel.thulium_radar");
    }
}
