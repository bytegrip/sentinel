package sh.myo.sentinel.item;

public class ItemGoldRadar extends ItemTieredRadar {
    public ItemGoldRadar() {
        super("Gold", 32, 50000, 38.0, "Gold Blocks", 4);
        setRegistryName("gold_radar");
        setTranslationKey("sentinel.gold_radar");
    }
}
