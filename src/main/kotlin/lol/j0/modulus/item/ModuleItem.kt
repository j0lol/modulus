package lol.j0.modulus.item

import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.onFailure
import lol.j0.modulus.api.AssembledModularTool
import lol.j0.modulus.api.DamageablePart
import lol.j0.modulus.api.Part
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class ModuleItem(settings: Settings?) : Item(settings) {
    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        val part = Part.deserialize(stack.orCreateNbt).onFailure { throw it.get() }

        return when (part) {
            is DamageablePart -> { part.repairIngredient.matchingStacks.contains(ingredient) }
            else -> false
        }
    }

    override fun getName(stack: ItemStack): Text {
        val part = Part.deserialize(stack.orCreateNbt).onFailure { throw it.get() }
        return part.material.name
    }

    @JvmField
    val maxDamage = 1
}
