package lol.j0.modulus.item;

import io.netty.util.AsyncMapping;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.ToolMaterials;
import lol.j0.modulus.ToolTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class ModuleItem extends Item {
	private static final List<ModuleItem> MODULES = new ArrayList<>();
	public MODULE_SIDE ModuleSide;

    public static Stream<ModuleItem> streamModules() {
		return MODULES.stream();
    }

    public enum MODULE_SIDE {
		A, // Left, Down
		B // Right, Up
	}

	public ModuleItem(MODULE_SIDE side, ToolMaterial material, Settings settings) {
		super(settings);

		this.ModuleSide = side;

		MODULES.add(this);
	}

	// todo: module speed. module enchantability. module mining level (Take ItemStacks)

	public static ToolMaterials getMaterial(ItemStack stack) {
		return ToolMaterials.fromString(stack.getOrCreateNbt().getString("material"));
	}

	public static ToolTypes getType(ItemStack stack) {
		return ToolTypes.fromString(stack.getOrCreateNbt().getString("type"));
	}





	public static String getModelID(ItemStack stack) {
		if (stack.getNbt() == null) {
			return "hologram";
		}

		var t = getType(stack).getModelName();
		var m = getMaterial(stack).getModelName();
		var name = stack.getOrCreateNbt().getString("tool_name");
		var side = stack.getOrCreateNbt().getString("side");
		var string = "";

		if ((name.contains("axe") && !name.contains("pickaxe")) || name.contains("hoe")) {
			if (t.equals("butt")) {
				if (Objects.equals(side, "b")) {side="a";} else {string += "flipped_";}
				string += "module_" + m + "_" + name.split("_")[1] + "_" + side;
				return string.toLowerCase();
			} else {
				if (Objects.equals(side, "a")) {side = "b";} else {string += "flipped_";}
				string += "module_" + m + "_" + t + "_" + side;
				return string.toLowerCase();
			}
		} else {
			string += "module_" + m + "_" + t + "_" + side;
			return string.toLowerCase();
		}
	}


	public ModuleItem(Settings settings) {
		super(settings);
	}
}
