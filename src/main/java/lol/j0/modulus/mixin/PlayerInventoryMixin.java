package lol.j0.modulus.mixin;

import lol.j0.modulus.item.ModularToolItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
	@Shadow
	public abstract ItemStack getMainHandStack();

	@Inject( at=@At("HEAD"), cancellable = true, method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F")
	private void getBlockBreakingSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
		var tool = this.getMainHandStack().getItem();
		if (tool instanceof ModularToolItem) {
			var stack = this.getMainHandStack();

			var speed = ModularToolItem.getMiningSpeed(stack);
			cir.setReturnValue(speed);

		}
	}
}
