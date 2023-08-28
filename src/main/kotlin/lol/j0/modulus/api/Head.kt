package lol.j0.modulus.api

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.onFailure
import lol.j0.modulus.Modulus
import lol.j0.modulus.registry.HeadMaterialRegistry
import net.minecraft.block.BlockState
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Ingredient
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

class Head(
    var side: ModuleSide,
    override var damage: Int,
    override val material: HeadMaterial,
    val materialId: Identifier,
): DamageablePart, Part {

    override val durability: Int
        get() = material.durability/2

    override val repairIngredient: Ingredient
        get() = material.repairIngredient

    override val zIndex = 1
    val partId = "modulus:head"

    val miningLevel: Int
        get() {
            return 9
        }

    fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return material.getMiningSpeedMultiplier.invoke(stack, state)
    }

    enum class ModuleSide {
        A,
        B;

        override fun toString(): String {
            return when (this) {
                A -> "a"
                B -> "b"
            }
        }

        companion object {
            fun fromString(string: String): ModuleSide? {
                return when (string) {
                    "a", "A" -> A
                    "b", "B" -> B
                    else -> null
                }
            }
        }


    }

    override fun serialize(): NbtCompound {
        val nbtCompound = NbtCompound()

        nbtCompound.putString("modulus:part", partId)
        nbtCompound.putString("modulus:material", materialId.toString())
        nbtCompound.putString("modulus:side", side.toString())
        nbtCompound.putInt("Damage", damage)


        return nbtCompound
    }

    fun isSuitable(state: BlockState): Boolean {
        return material.isSuitable.invoke(state)
    }

    fun useOnBlock(context: ItemUsageContext): ActionResult {
        return material.useOnBlock.invoke(context)
    }

    override fun getModelID(): ModelIdentifier =
        ModelIdentifier (
            Modulus.id (
                materialId.namespace.toString() + "/" +
                materialId.path.toString() + "/" +
                side.toString()
            ),
            "inventory"
        )
    companion object {
        fun deserialize(nbtCompound: NbtCompound): Result4k<Head, ModulusDeserializeException> {
            val materialId = Identifier(nbtCompound.getString("modulus:material"))
            return Success(
                Head(
                    side = ModuleSide.fromString(nbtCompound.getString("modulus:side")) ?: ModuleSide.A,
                    damage = nbtCompound.getInt("Damage"),
                    material = HeadMaterialRegistry.get(materialId).onFailure { return Failure(ModulusDeserializeException()) },
                    materialId = materialId
            ))
        }
    }
}
