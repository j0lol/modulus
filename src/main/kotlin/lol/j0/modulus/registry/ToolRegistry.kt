package lol.j0.modulus.registry

import lol.j0.modulus.api.RegisteredTool
import net.minecraft.item.Item
import net.minecraft.util.Identifier

object ToolRegistry {
    var TOOLS: HashMap<Identifier, RegisteredTool>? = null

    init {
        TOOLS = HashMap()
    }

    fun register(identifier: Identifier, item: Item?) {
        TOOLS!![identifier] = RegisteredTool(identifier, item!!)
    }
}
