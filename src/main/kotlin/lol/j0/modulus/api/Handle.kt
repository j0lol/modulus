package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import lol.j0.modulus.resource.DatagenUtils
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.nbt.NbtCompound

class Handle: Part {
    val PART_ID = "modulus:handle"

    var durability = 0
    var damage = 0
    override fun serialize(): NbtCompound {
        val nbtCompound = NbtCompound()

        nbtCompound.putString("modulus:part", PART_ID)
        nbtCompound.putString("modulus:material", "tool_rod")
        nbtCompound.putInt("modulus:damage", 0)
        return nbtCompound
    }

    override fun getModelID(): ModelIdentifier {
        return ModelIdentifier(Modulus.id("tool_rod"), "inventory")
    }
}
