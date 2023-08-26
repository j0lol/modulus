package lol.j0.modulus.api

import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

interface ModularTool {
    fun getPartLength() : Int
}
class DisassembledModularTool(private var headA: Head?, private var headB: Head?, private var handle: Handle?, private val enchantments: List<ToolEnchantment>?) : ModularTool {

    val parts: List<Part?>
        get() {
            return listOf(headA, headB, handle)
        }


    fun canAssemble(): Boolean {
        return parts.none {it == null}
    }

    override fun getPartLength() : Int {
        return parts.filterNotNull().size
    }

    fun addPart(part: Part?): Boolean {
        return when (part) {
            is Handle -> {
                if (handle == null) handle = part
                true
            }
            is Head -> {
                if (headA == null) {
                    part.side = Head.ModuleSide.A
                    headA = part
                    true
                } else if (headB == null) {
                    part.side = Head.ModuleSide.B
                    headB = part
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }

    fun removePart(): Part? {

        val partOnTool = parts.filterNotNull().firstOrNull()
        val part: Part? = partOnTool
        when (partOnTool) {
            headA -> headA = null
            headB -> headB = null
            handle -> handle = null
        }
        return part
    }

    fun serialize(): NbtCompound {
        val nbt = NbtCompound()

        nbt.putBoolean("modulus:is_editable", true)
        val moduleList = NbtList()

        for (part: Part? in parts) {
            if (part != null) {
                moduleList.add(part.serialize())
            }
        }
        val enchantmentList: NbtList? = if (enchantments != null) {
            val list = NbtList()
            for (enchant in enchantments) {
                list.add(enchant.serialize())
            }
            list
        } else {
            null
        }

        nbt.put("modulus:modules", moduleList)
        nbt.put("Enchantments", enchantmentList)
        return nbt
    }
    companion object {
        fun deserialize(nbt: NbtCompound): DisassembledModularTool {
            val moduleList = nbt.getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                Part.deserialize(it)
            }

            val enchantments: NbtList = nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE.toInt());

            val enchantmentList: List<ToolEnchantment> = enchantments.filterIsInstance<NbtCompound>().map {
                ToolEnchantment.deserialize(it)
            }

            return DisassembledModularTool(
                    partList.filterIsInstance<Head>().firstOrNull { head -> head.side == Head.ModuleSide.A },
                    partList.filterIsInstance<Head>().firstOrNull { head -> head.side == Head.ModuleSide.B },
                    partList.filterIsInstance<Handle>().firstOrNull(),
                    enchantmentList
            )
        }
    }

}
class AssembledModularTool(private val headA: Head, private val headB: Head, private val handle: Handle, private val enchantments: List<ToolEnchantment>?) : ModularTool {

    val parts: List<Part>
        get() {
            return listOf(headA, headB, handle)
        }

    fun getDurability(): Int {
        return 99
    }

    fun getDamage(): Int {
        return 0
    }

    val miningLevel = listOf(headA.miningLevel, headB.miningLevel).average().toInt()

    fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return listOf(headA, headB)
            .filter { head: Head -> head.isSuitable(state)  }
            .also { if (it.isEmpty()) return 1f }
            .map { head -> head.getMiningSpeedMultiplier(stack, state) }
            .average()
            .toFloat()
    }
    override fun getPartLength() : Int {
        return parts.size
    }

    fun serialize(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putBoolean("modulus:is_editable", false)
        val moduleList = NbtList()

        for (part: Part in parts) {
            moduleList.add(part.serialize())
        }

        val enchantmentList: NbtList? = if (enchantments != null) {
            val list = NbtList()
            for (enchant in enchantments) {
                list.add(enchant.serialize())
            }
            list
        } else {
            null
        }

        nbt.put("modulus:modules", moduleList)
        nbt.put("Enchantments", enchantmentList)
        return nbt
    }

    fun isSuitable(state: BlockState): Boolean {
        return (headA.isSuitable(state) || headB.isSuitable(state))
    }

    companion object {
        fun deserialize(nbt: NbtCompound): AssembledModularTool? {
            val moduleList = nbt.getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val enchantments: NbtList = nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE.toInt());

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                Part.deserialize(it)
            }

            val enchantmentList: List<ToolEnchantment> = enchantments.filterIsInstance<NbtCompound>().map {
                ToolEnchantment.deserialize(it)
            }

            return try {
                AssembledModularTool(
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.A },
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.B },
                        partList.filterIsInstance<Handle>().first(),
                        enchantmentList
                )
            } catch ( e: NoSuchElementException ) {
                throw DeserializeException("hey. you cant assemble a tool without the tool parts silly. what were you thinking. haha")
            }

        }
    }
}

class DeserializeException(s: String) : Exception(s) {
}