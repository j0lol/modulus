package lol.j0.modulus.item;

import lol.j0.modulus.Modulus;
import lol.j0.modulus.registry.ToolRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ClickType;
import net.minecraft.util.registry.Registry;

import static lol.j0.modulus.Modulus.MODULAR_TOOL;

public class ToolHammerItem extends Item {

	public boolean onClickedOnOther(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {

		boolean validConvertibleTool = (ToolRegistry.TOOLS).containsKey(Registry.ITEM.getId(slot.getStack().getItem()));
		//boolean validConvertibleTool = slot.getStack().isIn(TagKey.of(Registry.ITEM_KEY, Modulus.id("valid_tools")));

		if (slot.getStack().isOf(MODULAR_TOOL)) {
			ModularToolItem.toggleIfEditable(slot.getStack(), player);
			return true;
		} else if (validConvertibleTool) {
			slot.setStack(ModularToolItem.create( (ToolItem) slot.getStack().getItem()));
			return true;
		} else {
			return false;
		}
		//if (stack.isOf(Modulus.MODULAR_TOOL)) {
//			NbtCompound nbt = stack.getNbt();
//			if (nbt != null && nbt.getBoolean("Finished")) {
//				nbt.remove("Finished");
//				nbt.putBoolean("Finished", false);
//				return true;
//			} else if (nbt != null && !nbt.getBoolean("Finished")) {
//				nbt.remove("Finished");
//				nbt.putBoolean("Finished", true );
//				return true;
//			}
//		//}

	}

	public ToolHammerItem(Settings settings) {
		super(settings);
	}

}
