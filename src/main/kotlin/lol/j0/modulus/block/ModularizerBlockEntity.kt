package lol.j0.modulus.block

import lol.j0.modulus.Modulus
import lol.j0.modulus.gui.ImplementedInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction


class ModularizerBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Modulus.MODULARIZER_BLOCK_ENTITY, pos, state), ImplementedInventory, SidedInventory {

    private val items = DefaultedList.ofSize(2, ItemStack.EMPTY)

    override fun toString(): String {
        return "ModularizerBlockEntity()"
    }

    override fun markDirty() {
        TODO("Not yet implemented")
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
        val result = IntArray(getItems().size)
        for (i in result.indices) {
            result[i] = i
        }

        return result
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return true
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return true
    }

    override fun getItems(): DefaultedList<ItemStack> {
        return items
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, items)
    }

    override fun writeNbt(nbt: NbtCompound?) {
        Inventories.writeNbt(nbt, items)
        super.writeNbt(nbt)
    }

}