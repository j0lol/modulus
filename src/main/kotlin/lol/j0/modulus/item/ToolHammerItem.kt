package lol.j0.modulus.item

import lol.j0.modulus.Modulus.MODULAR_TOOL
import lol.j0.modulus.registry.ToolRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.Slot
import net.minecraft.util.ClickType

class ToolHammerItem(settings: Settings?) : Item(settings) {
    override fun onClickedOnOther(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        val validConvertibleTool = ToolRegistry.TOOLS!!.containsKey(Registries.ITEM.getId(slot.stack.item))
        //boolean validConvertibleTool = slot.getStack().isIn(TagKey.of(Registry.ITEM_KEY, Modulus.id("valid_tools")));
        return if (slot.stack.isOf(MODULAR_TOOL)) {
            ModularToolItem.toggleIfEditable(slot.stack, player)
            true
        } else if (validConvertibleTool) {
            slot.setStack(ModularToolItem.create(slot.stack.item as ToolItem))
            true
        } else {
            false
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
}
