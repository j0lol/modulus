package lol.j0.modulus.item

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.get
import lol.j0.modulus.Modulus
import lol.j0.modulus.Modulus.MODULAR_TOOL
import lol.j0.modulus.registry.ToolRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.Slot
import net.minecraft.util.ClickType

class QuillItem(settings: Settings?) : Item(settings) {
    override fun onClickedOnOther(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {

        return when {
            slot.stack.isOf(Modulus.TEMPLATE) -> {
                true
            }
            slot.stack.isOf(Items.PAPER) -> {
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
