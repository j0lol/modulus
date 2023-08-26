package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import lol.j0.modulus.registry.HeadMaterialRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

class Head(var side: ModuleSide, var damage: Int, var material: HeadMaterial): Part {
    override val zIndex = 1
    val PART_ID = "modulus:head"

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

        nbtCompound.putString("modulus:part", PART_ID)
        nbtCompound.putString("modulus:material", material.identifier.toString())
        nbtCompound.putString("modulus:side", side.toString())

        return nbtCompound
    }

    override fun getModelID(): ModelIdentifier = ModelIdentifier(
            Modulus.id(
                    material.identifier.namespace.toString() + "/" +
                            material.identifier.path.toString() + "/" +
                            side.toString()), "inventory")

    fun isSuitable(state: BlockState): Boolean {
        return material.isSuitable.invoke(state)
    }

    companion object {
        fun deserialize(nbtCompound: NbtCompound): Head {
            return Head(
                    ModuleSide.fromString(nbtCompound.getString("modulus:side")) ?: ModuleSide.A,
                    0,
                HeadMaterialRegistry.MATERIALS!![Identifier(nbtCompound.getString("modulus:material"))]!!
            )
        }
    }
}
