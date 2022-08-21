package lol.j0.modulus;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public enum ToolTypes {
	PICKAXE(BlockTags.PICKAXE_MINEABLE),
	AXE(BlockTags.AXE_MINEABLE),
	SHOVEL(BlockTags.SHOVEL_MINEABLE),
	SWORD(BlockTags.FIRE),
	HOE(BlockTags.HOE_MINEABLE),
	BUTT(BlockTags.FIRE);

	public final TagKey<Block> mineable;

	ToolTypes(TagKey<Block> i) {
		this.mineable = i;
	}

	public static ToolTypes fromString(String string) {
		return switch (string) {
			case "pickaxe" -> PICKAXE;
			case "axe" -> AXE;
			case "shovel" -> SHOVEL;
			case "sword" -> SWORD;
			case "hoe" -> HOE;
			case "butt" -> BUTT;
			default -> null;
		};
	}
	public String getModelName() {
		return this.toString().toLowerCase();
	}
}
