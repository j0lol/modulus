package lol.j0.modulus.api

import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

interface Part {
    fun serialize(): NbtCompound
    fun getModelID(): ModelIdentifier

}