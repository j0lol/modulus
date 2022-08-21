package lol.j0.modulus;

import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Lazy;

public enum ToolMaterials {
	WOOD(0, 59, 2.0F, 0.0F, 15 ),
	STONE(1, 131, 4.0F, 1.0F, 5 ),
	IRON(2, 250, 6.0F, 2.0F, 14),
	DIAMOND(3, 1561, 8.0F, 3.0F, 10),
	GOLD(0, 32, 12.0F, 0.0F, 22),
	NETHERITE(4, 2031, 9.0F, 4.0F, 15);


	public final int miningLevel;
	public final int itemDurability;
	public final float miningSpeed;
	public final float attackDamage;
	public final int enchantability;

	ToolMaterials(int i, int i1, float v, float v1, int i2) {


		this.miningLevel = i;
		this.itemDurability = i1;
		this.miningSpeed = v;
		this.attackDamage = v1;
		this.enchantability = i2;

	}
	public String getModelName() {
		if (this == WOOD || this == GOLD) {
			return this.toString().toLowerCase() + "en";
		}
		return this.toString().toLowerCase();
	}
	public static ToolMaterials fromString(String string) {
		return switch (string) {
			case "wood" -> WOOD;
			case "stone" -> STONE;
			case "iron" -> IRON;
			case "diamond" -> DIAMOND;
			case "gold" -> GOLD;
			case "netherite" -> NETHERITE;
			default -> null;
		};
	}

}
