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

    private val items = DefaultedList.ofSize(2, ItemStack.EMPTY)
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
        if (world.isClient) return ActionResult.SUCCESS
        val blockEntity = world.getBlockEntity(pos) as Inventory


        if (!player.getStackInHand(hand).isEmpty) {
            // Check what is the first open slot and put an item from the player's hand there
            if (blockEntity.getStack(0).isEmpty) {
                // Put the stack the player is holding into the inventory
                blockEntity.setStack(0, player.getStackInHand(hand).copy())
                // Remove the stack from the player's hand
                player.getStackInHand(hand).count = 0
            } else if (blockEntity.getStack(1).isEmpty) {
                blockEntity.setStack(1, player.getStackInHand(hand).copy())
                player.getStackInHand(hand).count = 0
            } else {
                // If the inventory is full we'll print its contents
                println(
                    "The first slot holds "
                            + blockEntity.getStack(0) + " and the second slot holds " + blockEntity.getStack(1)
                )
            }
        } else {
            // If the player is not holding anything we'll get give him the items in the block entity one by one

            // Find the first slot that has an item and give it to the player
            if (!blockEntity.getStack(1).isEmpty) {
                // Give the player the stack in the inventory
                player.inventory.offerOrDrop(blockEntity.getStack(1));
                // Remove the stack from the inventory
                blockEntity.removeStack(1);
            } else if (!blockEntity.getStack(0).isEmpty) {
                player.inventory.offerOrDrop(blockEntity.getStack(0));
                blockEntity.removeStack(0);
            }
        }

        return ActionResult.SUCCESS
    }

}