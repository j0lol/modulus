package lol.j0.modulus.api

import net.minecraft.recipe.Ingredient

interface DamageablePart : Part {

    val repairIngredient: Ingredient

    var damage: Int

    val broken: Boolean
        get() { return damage >= durability }

    val durability: Int
    fun damage(amount: Int) {
        if (!broken) damage += 1
    }
}