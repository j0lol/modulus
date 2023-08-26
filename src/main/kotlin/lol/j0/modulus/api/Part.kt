package lol.j0.modulus.api

import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

interface Part {


    val zIndex: Int;
    fun serialize(): NbtCompound
    fun getModelID(): ModelIdentifier

    companion object {
        fun deserialize(nbt: NbtCompound): Part? {
            return when (nbt.getString("modulus:part")) {
                "modulus:head" -> Head.deserialize(nbt)
                "modulus:handle" -> Handle()
                else -> null
            }
        }
    }


}