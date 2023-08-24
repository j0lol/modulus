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

    fun serialize(): ItemStack {
        val modTool = Modulus.MODULAR_TOOL.defaultStack
        modTool.getOrCreateNbt().putBoolean("modulus:is_editable", true)
        val moduleList = modTool.getOrCreateNbt().getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

        for (part: Part? in parts) {
            if (part != null) {
                moduleList.add(part.serialize())
            }
        }

        return modTool
    }
    companion object {
        fun deserialize(stack: ItemStack): DisassembledModularTool {
            val moduleList = stack.getOrCreateNbt().getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                when (it.getString("modulus:part")) {
                    "modulus:head" -> Head.deserialize(it)
                    "modulus:handle" -> Handle()
                    else -> null
                }
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

    fun serialize(): ItemStack {
        val modTool = Modulus.MODULAR_TOOL.defaultStack
        modTool.getOrCreateNbt().putBoolean("modulus:is_editable", false)
        val moduleList = NbtList()

        for (part: Part in parts) {
            moduleList.add(part.serialize())
        }

        modTool.getOrCreateNbt().put("modulus:modules", moduleList)
        Modulus.LOGGER.info(modTool.getOrCreateNbt().toString())
        Modulus.LOGGER.info(parts.toString())
        Modulus.LOGGER.info(moduleList.toString())

        return modTool
    }
    companion object {

        fun deserialize(stack: ItemStack): AssembledModularTool? {
            val moduleList = stack.getOrCreateNbt().getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())

            val partList = moduleList.filterIsInstance<NbtCompound>().mapNotNull {
                when (it.getString("modulus:part")) {
                    "modulus:head" -> Head.deserialize(it)
                    "modulus:handle" -> Handle()
                    else -> null
                }
            }

            return try {
                AssembledModularTool(
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.A },
                        partList.filterIsInstance<Head>().first { head -> head.side == Head.ModuleSide.B },
                        partList.filterIsInstance<Handle>().first()
                )
            } catch ( e: NoSuchElementException ) {
                throw Exception("hey. you cant assemble a tool without the tool parts silly. what were you thinking.")
            }

        }

//        fun deserialize(stack: ItemStack): AssembledModularTool {
//            val moduleList = stack.getOrCreateNbt().getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())
//            val idA = (moduleList[0] as NbtCompound).getString("modulus:identifier")
//            val idB = (moduleList[1] as NbtCompound).getString("modulus:identifier")
//            return AssembledModularTool(
//                Head(Head.ModuleSide.A, 0, HeadMaterial(Identifier(idA))),
//                Head(Head.ModuleSide.B, 0, HeadMaterial(Identifier(idB))),
//                Handle()
//            )
//        }

    }
}