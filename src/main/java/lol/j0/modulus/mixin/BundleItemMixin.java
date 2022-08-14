package lol.j0.modulus.mixin;


import lol.j0.modulus.Modulus;
import lol.j0.modulus.item.ModularToolItem;
import lol.j0.modulus.item.UnfinishedModularToolItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin  {

	@Inject(at=@At("HEAD"), method= "playInsertSound(Lnet/minecraft/entity/Entity;)V", cancellable = true)
	private void playInsertSound(Entity entity, CallbackInfo ci) {
		if ((BundleItem) (Object) this instanceof UnfinishedModularToolItem) {
			entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
			ci.cancel();
		}
	}
	@Inject(at=@At("HEAD"), method= "playRemoveOneSound(Lnet/minecraft/entity/Entity;)V", cancellable = true)

	private void playRemoveOneSound(Entity entity, CallbackInfo ci) {
		if ((BundleItem) (Object) this instanceof UnfinishedModularToolItem) {
			entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
			ci.cancel();
		}
	}



}
