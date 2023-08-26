package lol.j0.modulus.mixin;

import lol.j0.modulus.item.ModularToolItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Shadow
	public abstract PlayerInventory getInventory();

	@Inject( at=@At("HEAD"), cancellable = true, method = "canHarvest(Lnet/minecraft/block/BlockState;)Z")
	private void modulus_canHarvest(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		var tool = this.getInventory().getMainHandStack().getItem();
		if (tool instanceof ModularToolItem) {
			var stack = this.getInventory().getMainHandStack();

			cir.setReturnValue(!state.isToolRequired() || ModularToolItem.Companion.isSuitable(stack, state));
//
//			var stack = this.getInventory().getMainHandStack();
//
//			if ( !state.isToolRequired() ) {
//				cir.setReturnValue(true);
//			} else {
//				var level = modular_tool.getMiningLevel(stack);
//				if (level < 3 && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
//					cir.setReturnValue(false);
//				} else if (level < 2 && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
//					cir.setReturnValue(false);
//				} else {
//					var moduleList = ModularToolItem.getModuleList(stack);
//
//
//					for (NbtElement module: moduleList) {
//						var moduleStack = ItemStack.fromNbt((NbtCompound) module);
//
//						if (moduleStack.getNbt() != null) {
//						}
//						if (moduleStack.getNbt() != null && moduleStack.getNbt().getString("type").equals("pickaxe")) {
//							if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
//								cir.setReturnValue(true);
//							}
//						} else if (moduleStack.getNbt() != null && moduleStack.getNbt().getString("type").equals("axe")) {
//							if (state.isIn(BlockTags.AXE_MINEABLE)) {
//								cir.setReturnValue(true);
//							}
//						}else if (moduleStack.getNbt() != null && moduleStack.getNbt().getString("type").equals("shovel")) {
//							if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
//								cir.setReturnValue(true);
//							}
//						} else if (moduleStack.getNbt() != null && moduleStack.getNbt().getString("type").equals("hoe")) {
//							if (state.isIn(BlockTags.HOE_MINEABLE)) {
//								cir.setReturnValue(true);
//							}
//						}else {
//							cir.setReturnValue(false);
//
//						}
//
//
//					}
//
//				}
//			}
			cir.cancel();

		}

	}
}
