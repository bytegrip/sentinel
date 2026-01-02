package sh.myo.sentinel.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sh.myo.sentinel.Sentinel;
import sh.myo.sentinel.Tags;

public class ItemSentinelRadar extends Item {

    private static final int COOLDOWN_TICKS = 200;
    private static final String NBT_COOLDOWN = "CooldownTime";

    public ItemSentinelRadar() {
        setRegistryName(Tags.MOD_ID, "sentinel_radar");
        setTranslationKey(Tags.MOD_ID + ".sentinel_radar");
        setCreativeTab(sh.myo.sentinel.SentinelTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (!worldIn.isRemote) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            
            long currentTime = worldIn.getTotalWorldTime();
            long lastUseTime = nbt.getLong(NBT_COOLDOWN);
            long timeSinceUse = currentTime - lastUseTime;
            
            if (timeSinceUse < COOLDOWN_TICKS) {
                long remaining = (COOLDOWN_TICKS - timeSinceUse) / 20;
                playerIn.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Radar cooling down... " + remaining + "s remaining"));
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            
            nbt.setLong(NBT_COOLDOWN, currentTime);
        }
        
        if (worldIn.isRemote) {
            playerIn.openGui(Sentinel.instance, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
