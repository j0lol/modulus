package lol.j0.modulus.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import lol.j0.modulus.Modulus
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class QuillGuiDescription(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext): SyncedGuiDescription(
    Modulus.SCREEN_HANDLER_TYPE,
    syncId,
    playerInventory,
    getBlockInventory(context, 2),
    getBlockPropertyDelegate(context)
) {

}