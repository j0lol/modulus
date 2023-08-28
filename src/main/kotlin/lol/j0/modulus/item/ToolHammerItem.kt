package lol.j0.modulus.item

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.get
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

        // todo: use tags, maybe?
        val validConvertibleTool = ToolRegistry.TOOLS!!.containsKey(Registries.ITEM.getId(slot.stack.item))
        //boolean validConvertibleTool = slot.getStack().isIn(TagKey.of(Registry.ITEM_KEY, Modulus.id("valid_tools")));
        return when {
            slot.stack.isOf(MODULAR_TOOL) -> {
                ModularToolItem.toggleIfEditable(slot.stack, player)
                true
            }
            validConvertibleTool -> {
                when (val item = ModularToolItem.create(slot.stack)) {
                    is Failure -> false
                    is Success -> {
                        slot.stack = item.get()
                        true
                    }
                }
            }
            else -> {
                false
            }
        }
    }
}
