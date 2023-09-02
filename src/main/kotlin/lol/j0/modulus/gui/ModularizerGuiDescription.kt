package lol.j0.modulus.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import lol.j0.modulus.Modulus
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType


const val INVENTORY_SIZE  = 2

class ModularizerGuiDescription(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext) :
    SyncedGuiDescription(
        Modulus.SCREEN_HANDLER_TYPE,
        syncId,
        playerInventory,
        getBlockInventory(context, INVENTORY_SIZE),
        getBlockPropertyDelegate(context)
    ) {

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(200, 200)
        root.setInsets(Insets.ROOT_PANEL)
        val itemSlot = WItemSlot.of(blockInventory, 0)
        root.add(itemSlot, 4, 1)
        root.add(WItemSlot.of(blockInventory, 1), 5, 1)
        root.add(this.createPlayerInventoryPanel(), 0, 3)
        root.validate(this)
    }

}
