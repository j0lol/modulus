package lol.j0.modulus.mixin;


import lol.j0.modulus.item.ModularToolItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.BundleItem;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleItem.class)
public class BundleItemMixin  {

	@Inject(at=@At("HEAD"), method= "playInsertSound(Lnet/minecraft/entity/Entity;)V", cancellable = true)
	private void playInsertSound(Entity entity, CallbackInfo ci) {
		if ((BundleItem) (Object) this instanceof ModularToolItem) {
			entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
			ci.cancel();
		}
	}
	@Inject(at=@At("HEAD"), method= "playRemoveOneSound(Lnet/minecraft/entity/Entity;)V", cancellable = true)

	private void playRemoveOneSound(Entity entity, CallbackInfo ci) {
		if ((BundleItem) (Object) this instanceof ModularToolItem) {
			entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
			ci.cancel();
		}
	}



}
