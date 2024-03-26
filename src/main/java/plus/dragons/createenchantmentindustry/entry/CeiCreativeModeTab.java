package plus.dragons.createenchantmentindustry.entry;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;

public class CeiCreativeModeTab extends CreativeModeTab {

    public CeiCreativeModeTab() {
        super(EnchantmentIndustry.ID + ".base");
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        items.add(CeiBlocks.DISENCHANTER.asStack());
        items.add(CeiBlocks.PRINTER.asStack());
        items.add(CeiItems.ENCHANTING_GUIDE.asStack());
        items.add(CeiItems.EXPERIENCE_ROTOR.asStack());
        items.add(CeiFluids.INK.get().getBucket().getDefaultInstance());
        items.add(CeiItems.HYPER_EXP_BOTTLE.asStack());
    }


    @Override
    public @NotNull ItemStack makeIcon() {
        return CeiItems.ENCHANTING_GUIDE.asStack();
    }
}
