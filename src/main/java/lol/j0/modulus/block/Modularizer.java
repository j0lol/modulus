package lol.j0.modulus.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Modularizer extends StonecutterBlock {
	private static final Text GUI_TITLE = Text.translatable("container.modulus.modularizer");
	private static final VoxelShape SHAPE = VoxelShapes.fullCube();

	public Modularizer(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

//	@Override
//	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//		if (world.isClient()) {
//			return ActionResult.SUCCESS;
//		} else {
//			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
//			return ActionResult.CONSUME;
//		}
//	}

//	@Nullable
//	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
//		return new SimpleNamedScreenHandlerFactory(
//				(syncId, playerInventory, player) -> new ModularizerScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)), GUI_TITLE
//		);
//	}

}
