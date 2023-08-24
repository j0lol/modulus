package lol.j0.modulus.api

import lol.j0.modulus.registry.ToolRegistry
import net.minecraft.item.Item
import net.minecraft.util.Identifier

class RegisteredTool(var identifier: Identifier, var item: Item) {

    val miningLevel: Int
        get() = 99

}
