package lol.j0.modulus.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class QuillScreen(gui: QuillGuiDescription, player: PlayerEntity, title: Text):
    CottonInventoryScreen<QuillGuiDescription>(gui, player, title) {
}