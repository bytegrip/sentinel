package sh.myo.sentinel.item;

public class ItemWoodenRadar extends ItemTieredRadar {
    public ItemWoodenRadar() {
        super("Wooden", 32, 1000, 60.0, "Logs", 32);
        setRegistryName("wooden_radar");
        setTranslationKey("sentinel.wooden_radar");
    }
}
