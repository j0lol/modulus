package lol.j0.modulus.api

import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.recipe.Ingredient
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction2

class HandleMaterial(
    val durability: Int,
    val repairIngredient: Ingredient
): PartMaterial {
    override val name: Text
        get() = Text.literal("Wooden Handle")
}
