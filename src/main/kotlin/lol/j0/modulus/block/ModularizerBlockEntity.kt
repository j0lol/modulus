package lol.j0.modulus.block

import ModularizerBlock
import lol.j0.modulus.Modulus
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

class ModularizerBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos,
    state
) {
//    constructor(pos: BlockPos, state: BlockState) : super(Modulus.MODULARIZER_BLOCK_ENTITY, pos, state)
    override fun toString(): String {
        return "ModularizerBlockEntity()"
    }
}