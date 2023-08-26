package lol.j0.modulus.api

import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2

class HeadMaterial(
    var identifier: Identifier,
    var getMiningSpeedMultiplier: KFunction2<ItemStack, BlockState, Float>,
    var isSuitable: Function1<BlockState, Boolean>

)
