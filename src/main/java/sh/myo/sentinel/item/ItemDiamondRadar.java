package sh.myo.sentinel.item;

public class ItemDiamondRadar extends ItemTieredRadar {
    public ItemDiamondRadar() {
        super("Diamond", 32, 100000, 32.0, "Diamond Blocks", 4);
        setRegistryName("diamond_radar");
        setTranslationKey("sentinel.diamond_radar");
    }
}
