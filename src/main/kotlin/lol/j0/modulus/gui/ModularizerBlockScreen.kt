package lol.j0.modulus.gui

import ModularizerBlock
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class ModularizerBlockScreen(gui: ModularizerGuiDescription, player: PlayerEntity, title: Text) : CottonInventoryScreen<ModularizerGuiDescription>(gui, player, title)