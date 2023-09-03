package lol.j0.modulus.item

import lol.j0.modulus.Modulus
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot
import net.minecraft.util.ClickType

class QuillItem(settings: Settings?) : Item(settings) {
    override fun onClickedOnOther(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {

        return when {
            slot.stack.isOf(Modulus.TEMPLATE) -> {
                true
            }
            slot.stack.isOf(Items.PAPER) -> {
                slot.stack = Modulus.TEMPLATE.defaultStack
                true
            }
            else -> {
                false
            }
        }
    }
}
