package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import lol.j0.modulus.item.ModuleItem
import lol.j0.modulus.resource.DatagenUtils
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

class Head(var side: ModuleSide, var damage: Int, var material: HeadMaterial): Part {

    val PART_ID = "modulus:head"
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

    override fun getModelID(): ModelIdentifier {

        Modulus.LOGGER.info(material.identifier.toString())
        return ModelIdentifier(
                Modulus.id(
                        material.identifier.namespace.toString() + "/" +
                                material.identifier.path.toString() + "/" +
                                side.toString()), "inventory")
    }

    companion object {
        fun deserialize(nbtCompound: NbtCompound): Head {
            return Head(
                    ModuleSide.fromString(nbtCompound.getString("modulus:side")) ?: ModuleSide.A,
                    0,
                    HeadMaterial(Identifier(nbtCompound.getString("modulus:material")))
            )
        }
    }
}
