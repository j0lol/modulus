package lol.j0.modulus.item;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;


public class ModuleItem extends Item {

	public MODULE_SIDE ModuleSide;
	public enum MODULE_SIDE {
		A, // Left, Down
		B // Right, Up
	}

	public ModuleItem(MODULE_SIDE side, ToolMaterial material, Settings settings) {
		super(settings);

		this.ModuleSide = side;
	}
	public ModuleItem(Settings settings) {
		super(settings);
	}
}
