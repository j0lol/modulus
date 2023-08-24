package lol.j0.modulus.registry

import lol.j0.modulus.api.Material
import net.minecraft.util.Identifier

object MaterialRegistry {
    var MATERIALS: HashMap<Identifier, Material>? = null

    init {
        MATERIALS = HashMap()
    }
}
