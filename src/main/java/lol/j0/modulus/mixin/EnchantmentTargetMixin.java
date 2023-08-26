package lol.j0.modulus.mixin;

import lol.j0.modulus.item.ModularToolItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$C_gdezogus")
public abstract class EnchantmentTargetMixin {

	@Inject( at=@At("HEAD"), cancellable = true, method = "isAcceptableItem")
	private void modulus_isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir) {

		if (item instanceof ModularToolItem) {
			cir.setReturnValue(true);
		}

		cir.cancel();
	}
}
