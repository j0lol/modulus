package lol.j0.modulus.registry

import lol.j0.modulus.api.HeadMaterial
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction2

object HeadMaterialRegistry {
    var MATERIALS: HashMap<Identifier, HeadMaterial>? = null

    init {
        MATERIALS = HashMap()
    }

    fun register(identifier: Identifier, miningSpeed: KFunction2<ItemStack, BlockState, Float>, isSuitable: Function1<BlockState, Boolean>) {
        MATERIALS!![identifier] = HeadMaterial(identifier, miningSpeed, isSuitable)
    }
}
