package lol.j0.modulus.api;

import lol.j0.modulus.registry.MaterialRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class Material {
	public final int enchantability;

	public Ingredient repairIngredient;
	public Material(int enchantability, Ingredient repairIngredient) {
		this.enchantability = enchantability;
		this.repairIngredient = repairIngredient;
	}

//	public static void register(ToolItem toolItem) {
//		var ingredient = toolItem.getMaterial().getRepairIngredient();
//
//		if (MaterialRegistry.MATERIALS.get(ingredient.) == null) {
//			MaterialRegistry.MATERIALS.put(identifier, new Material(toolItem.getEnchantability(), toolItem.getMaterial().getRepairIngredient()));
//		}
//	}
}
