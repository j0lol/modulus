package lol.j0.modulus.block;

import lol.j0.modulus.Modulus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Adapted from Tablesaw code. Will probably need heavy adaptation before it can be used for Modularizer purposes.
 * Do not commit and use before understanding and reworking if needed.
 *
 * @author DebuggyTeam
 *
 */
public class ModularizerScreenHandler extends ScreenHandler {

	protected ModularizerScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
		return null;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return false;
	}
}
