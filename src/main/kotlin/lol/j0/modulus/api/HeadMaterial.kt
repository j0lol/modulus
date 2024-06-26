package lol.j0.modulus.api

import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.recipe.Ingredient
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction2

class HeadMaterial(
    var getMiningSpeedMultiplier: KFunction2<ItemStack, BlockState, Float>,
    var isSuitable: (BlockState) -> Boolean,
    var useOnBlock: (ItemUsageContext) -> ActionResult,
    val repairIngredient: Ingredient,
    val durability: Int,
    override val name: Text
): PartMaterial {
}