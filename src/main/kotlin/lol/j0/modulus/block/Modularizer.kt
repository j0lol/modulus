import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.StonecutterBlock
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class Modularizer(settings: Settings?) : StonecutterBlock(settings) {
    val GUI_TITLE: Text = Text.translatable("container.modulus.modularizer")
    val SHAPE = VoxelShapes.fullCube()

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return SHAPE
    }

    // todo libGUI
}