//package lol.j0.modulus.mixin;
//
//import lol.j0.modulus.item.ModularToolItem;
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.tag.BlockTags;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(PlayerInventory.class)
//public abstract class PlayerInventoryMixin {
//	@Shadow
//	public abstract ItemStack getMainHandStack();
//
//	@Inject( at=@At("HEAD"), method= "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F")
//	private void getBlockBreakingSpeed(BlockState state, CallbackInfoReturnable<Boolean> cir) {
//		var tool = this.getMainHandStack().getItem();
//		if (tool instanceof ModularToolItem modular_tool) {
//			var stack = this.getMainHandStack();
//
//			var moduleList = ModularToolItem.getModuleList(stack);
//			Float[] speed = new Float[2];
//			for (int i = 0, moduleListSize = moduleList.size(); i < moduleListSize; i++) {
//				NbtElement module = moduleList.get(i);
//				var moduleStack = ItemStack.fromNbt((NbtCompound) module);
//				var material = moduleStack.getOrCreateNbt().getString("material");
//
//				speed[i] = 0f;
//			}
//
//			if (material.equals("wooden")) {
//				speed = 0f;
//			} else if (material.equals("stone")) {
//				speed = 1.0f;
//			}
//		}
//	}
//}
