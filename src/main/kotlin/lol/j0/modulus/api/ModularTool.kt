package lol.j0.modulus.api

import lol.j0.modulus.Modulus
import lol.j0.modulus.item.ModularToolItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.Identifier

interface ModularTool {
    fun getPartLength() : Int
}
class DisassembledModularTool(var headA: Head?, var headB: Head?, var handle: Handle?) : ModularTool {

    val parts: List<Part?>
        get() {
            return listOf(headA, headB, handle)
        }


    fun canAssemble(): Boolean {
        return parts.none {it == null}
    }

    fun assemble(): AssembledModularTool {
        if (!canAssemble()) throw Exception("Tool attempted to be assembled without all parts present")
        return AssembledModularTool(headA!!, headB!!, handle!!);
    }

    override fun getPartLength() : Int {
        return parts.filterNotNull().size
    }

    fun serialize(): NbtCompound {
        val nbt = NbtCompound()

        nbt.putBoolean("modulus:is_editable", true)
        val moduleList = nbt.getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

        for (part: Part? in parts) {
            if (part != null) {
                moduleList.add(part.serialize())
            }
        }

        return nbt
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
        TODO("im tired ok")
    }


    companion object {
        fun deserialize(nbt: NbtCompound): DisassembledModularTool {
            val moduleList = nbt.getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                Part.deserialize(it)
            }

            return DisassembledModularTool(
                    partList.filterIsInstance<Head>().firstOrNull { head -> head.side == Head.ModuleSide.A },
                    partList.filterIsInstance<Head>().firstOrNull { head -> head.side == Head.ModuleSide.B },
                    partList.filterIsInstance<Handle>().firstOrNull()
            )
        }
    }

}
class AssembledModularTool(val headA: Head, val headB: Head, val handle: Handle) : ModularTool {

    val parts: List<Part>
        get() {
            return listOf(headA, headB, handle)
        }
    fun disassemble(): DisassembledModularTool {
        return DisassembledModularTool(headA, headB, handle);
    }

    fun getDurability(): Int {
        return 99
    }

    fun getDamage(): Int {
        return 0
    }
    fun getMiningLevel(): Int {
        return 99
    }

    fun getMiningSpeed(): Float {
        return 6f
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

        nbt.put("modulus:modules", moduleList)
        Modulus.LOGGER.info(nbt.toString())
        Modulus.LOGGER.info(parts.toString())
        Modulus.LOGGER.info(moduleList.toString())

        return nbt
    }
    companion object {

        fun deserialize(nbt: NbtCompound): AssembledModularTool? {
            val moduleList = nbt.getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                Part.deserialize(it)
            }

            return try {
                AssembledModularTool(
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.A },
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.B },
                        partList.filterIsInstance<Handle>().first()
                )
            } catch ( e: NoSuchElementException ) {
                throw Exception("hey. you cant assemble a tool without the tool parts silly. what were you thinking. haha")
            }

        }
    }
}