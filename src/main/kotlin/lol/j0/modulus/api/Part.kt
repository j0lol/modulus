package lol.j0.modulus.api

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

interface Part {


    val zIndex: Int;
    fun serialize(): NbtCompound
    fun getModelID(): ModelIdentifier

    val material: PartMaterial

    companion object {
        fun deserialize(nbt: NbtCompound): Result<Part, ModulusDeserializeException> {
            return when (nbt.getString("modulus:part")) {
                "modulus:head" -> Head.deserialize(nbt)
                "modulus:handle" -> Handle.deserialize(nbt)
                else -> Failure(ModulusDeserializeException())
            }
        }
    }


}