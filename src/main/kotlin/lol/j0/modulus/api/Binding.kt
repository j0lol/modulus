package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.nbt.NbtCompound

class Binding : Part {
    var durability = 0
    var damage = 0
    override fun serialize(): NbtCompound {
        return NbtCompound()
    }

    override fun getModelID(): ModelIdentifier {
        return ModelIdentifier(Modulus.id("binding"), "inventory")

    }
}
