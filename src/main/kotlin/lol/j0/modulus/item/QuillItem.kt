package lol.j0.modulus.item

import lol.j0.modulus.Modulus
import lol.j0.modulus.gui.QuillGuiDescription
import lol.j0.modulus.gui.QuillScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.ClickType

class QuillItem(settings: Settings?) : Item(settings), NamedScreenHandlerFactory {
    override fun onClickedOnOther(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {

        return when {
            slot.stack.isOf(Modulus.TEMPLATE) -> {
                true
            }
            slot.stack.isOf(Items.PAPER) -> {
                MinecraftClient.getInstance().setScreen(
                    QuillScreen(
                        Modulus.QUILL_SCREEN_HANDLER.create(1, player.inventory),
                        player,
                        displayName
                    )
                )
                //slot.stack = Modulus.TEMPLATE.defaultStack
                true
            }
            else -> {
                false
            }
        }
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity): ScreenHandler {
        return QuillGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(playerEntity.world, playerEntity.blockPos) )
    }

    override fun getDisplayName(): Text {
        return Text.translatable(translationKey)
    }
}
