package lol.j0.modulus.test

import lol.j0.modulus.Modulus
import lol.j0.modulus.api.AssembledModularTool
import lol.j0.modulus.api.DisassembledModularTool
import lol.j0.modulus.item.ModularToolItem
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.nbt.NbtCompound
import org.quiltmc.qkl.library.nbt.set

class SerializationTest {


    companion object {
        fun test() {
//
//            Modulus.LOGGER.info("Running Modulus serialization tests")
//            val testToolItem = Items.DIAMOND_PICKAXE as ToolItem
//            val testTool = ModularToolItem.create(testToolItem.defaultStack)
//
//            //assert(testTool.nbt == AssembledModularTool.deserialize(testTool.nbt!!)!!.serialize())
//
//            testTool.nbt!!["modulus:is_editable"] = true
//
//            val deserToolA = AssembledModularTool.deserialize(testTool.nbt!!)
//            val serToolA = deserToolA?.serialize()
//
//            val deserToolD = DisassembledModularTool.deserialize(testTool.nbt!!)
//            val serToolD = deserToolD.serialize()
//
//            assert(testTool.nbt == DisassembledModularTool.deserialize(testTool.nbt!!).serialize())
//
//            val tool = DisassembledModularTool.deserialize(testTool.nbt!!)
//            assert(tool.getPartLength() == 3)
//            val removedPart = tool.removePart()
//            assert(tool.getPartLength() == 2)
//            tool.addPart(removedPart)
//            assert(tool.getPartLength() == 3)
//
//            val newTool = Modulus.MODULAR_TOOL.defaultStack
//            newTool.nbt = tool.serialize()
//            assert(testTool == newTool)
        }

    }
}