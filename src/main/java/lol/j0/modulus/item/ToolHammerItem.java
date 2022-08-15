package lol.j0.modulus.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

public class ToolHammerItem extends Item {

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {

		//if (stack.isOf(Modulus.MODULAR_TOOL)) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null && nbt.getBoolean("Finished")) {
				nbt.remove("Finished");
				nbt.putBoolean("Finished", false);
				return true;
			} else if (nbt != null && !nbt.getBoolean("Finished")) {
				nbt.remove("Finished");
				nbt.putBoolean("Finished", true );
				return true;
			}
		//}

		return false;
	}

	public ToolHammerItem(Settings settings) {
		super(settings);
	}

}
