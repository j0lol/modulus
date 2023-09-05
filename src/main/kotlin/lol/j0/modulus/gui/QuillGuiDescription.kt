package lol.j0.modulus.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import lol.j0.modulus.Modulus
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text

class QuillGuiDescription(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext): SyncedGuiDescription(
    Modulus.QUILL_SCREEN_HANDLER,
    syncId,
    playerInventory,
    getBlockInventory(context, 2),
    getBlockPropertyDelegate(context)
) {

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(400, 300)
        root.setInsets(Insets.ROOT_PANEL)
//        val itemSlot = WItemSlot.of(blockInventory, 0)
//        root.add(itemSlot, 4, 1)
//        root.add(WItemSlot.of(blockInventory, 1), 5, 1)
//        root.add(this.createPlayerInventoryPanel(), 0, 3)

        val buttonList = WGridPanel()
        buttonList.add(WButton(Text.literal("evil button")), 0, 0, 20, 20)

        root.add(buttonList, 0, 2)

        root.validate(this)
    }
}