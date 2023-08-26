package lol.j0.modulus.api

import net.minecraft.enchantment.Enchantment
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import org.quiltmc.qkl.library.nbt.set

class ToolEnchantment(val enchantment: Identifier, val level: Int) {

    fun serialize(): NbtCompound {
        val nbt = NbtCompound()

        nbt["id"] = enchantment.toString()
        nbt["lvl"] = level

        return nbt

    }
    companion object {
        fun deserialize(nbt: NbtCompound): ToolEnchantment {
            val enchantId = Identifier(nbt.getString("id"))
            return ToolEnchantment(enchantId, nbt.getInt("lvl"))
        }
    }
}