package lol.j0.modulus.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.StackReference
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.screen.slot.Slot
import net.minecraft.util.ClickType
import net.minecraft.util.Identifier
import java.util.*

class ToolRodItem(settings: Settings?) : Item(settings) {
    override fun onClicked(stack: ItemStack, otherStack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity, cursorStackReference: StackReference): Boolean {
        val PlankNames = Hashtable<String, Item>()
        val tool_rod_list = guessPlankItemNames()
        for (i in tool_rod_list) {
            when (i) {
                "acacia" -> PlankNames[i] = Items.ACACIA_PLANKS
                "birch" -> PlankNames[i] = Items.BIRCH_PLANKS
                "crimson" -> PlankNames[i] = Items.CRIMSON_PLANKS
                "dark_oak" -> PlankNames[i] = Items.DARK_OAK_PLANKS
                "jungle" -> PlankNames[i] = Items.JUNGLE_PLANKS
                "mangrove" -> PlankNames[i] = Items.MANGROVE_PLANKS
                "oak" -> PlankNames[i] = Items.OAK_PLANKS
                "spruce" -> PlankNames[i] = Items.SPRUCE_PLANKS
                "warped" -> PlankNames[i] = Items.WARPED_PLANKS
                else -> PlankNames[i] = Items.AIR
            }
        }

//		var stick_material = stack.getOrCreateNbt().getString("material");
//		if (Objects.equals(stick_material, "vanilla") || stick_material.equals("default")) {
//			player.dropItem(Items.STICK);
//		} else if (PlankNames.containsKey(stick_material)) {
//			player.dropItem(PlankNames.get(stick_material));
//		}
        if (otherStack.isIn(planks)) {
            stack.getOrCreateNbt().putString("material", otherStack.item.toString().split("_planks".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            otherStack.decrement(1)
        } else if (otherStack.isOf(Items.STICK)) {
            stack.getOrCreateNbt().putString("material", "vanilla")
            otherStack.decrement(1)
        } else {
            return false
        }
        return true
    }

    fun guessPlankItemNames(): ArrayList<String> {
        val resources = arrayOf("acacia", "birch", "crimson", "dark_oak", "jungle", "mangrove", "oak", "spruce", "warped")
        val out = ArrayList<String>()
        for (r in resources) {
            out.add(r + "_planks")
        }
        return out
    }

    companion object {
        var planks = TagKey.of(Registries.ITEM.key, Identifier("minecraft", "planks"))
    }
}
