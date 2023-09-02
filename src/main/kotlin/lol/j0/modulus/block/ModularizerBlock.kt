import lol.j0.modulus.block.ModularizerBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.command.argument.BlockPosArgumentType.blockPos
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess


class ModularizerBlock(settings: Settings?) : BlockWithEntity(settings), InventoryProvider {
    val GUI_TITLE: Text = Text.translatable("container.modulus.modularizer")
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ModularizerBlockEntity(pos, state)
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return ModularizerBlockEntity(pos, state)
    }

    /**
     * i copied this all from fabric tutorial
     */
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        return ActionResult.SUCCESS
    }

}
