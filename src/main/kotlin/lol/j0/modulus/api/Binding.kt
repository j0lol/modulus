package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Ingredient

class Binding(override val repairIngredient: Ingredient) : Part, DamageablePart {

    override val zIndex = 2
    override var durability = 0
    override var damage = 0

    override fun serialize(): NbtCompound {
        return NbtCompound()
    }

    override fun getModelID(): ModelIdentifier {
        return ModelIdentifier(Modulus.id("binding"), "inventory")

    }

    override val material: PartMaterial
        get() = TODO("Not yet implemented")

}
