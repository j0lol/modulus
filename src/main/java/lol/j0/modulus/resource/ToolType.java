package lol.j0.modulus.resource;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lol.j0.modulus.Modulus;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.mixin.AbstractBlockAccessor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ToolType {
	private final Identifier id;
	private final String pathName;
	public static final List<ToolType> TYPES;
	public static final List<Tool> DISCOVERED_TOOLS;

	static {
		TYPES = new ArrayList<>();
		DISCOVERED_TOOLS = new ArrayList<>();
	}

	public Identifier getId() {
	return this.id;
	}

	public String getName() {
	return this.id.toString().split(":")[1];
	}


	public ToolType(Identifier id) {
		this.id = id;
		this.pathName = getPathName(this.id);
	}

	public String getPathName() {
	return this.pathName;
	}

	private static String getPathName(Identifier id) {
		var path = id.getPath();
		var namespace = id.getNamespace();
		if (!namespace.equals("minecraft") && !namespace.equals(Modulus.MOD_ID))
			path = namespace + '/' + path;
		return path;
	}

	public static void onItemRegister(Identifier id, ToolItem item) {
		Modulus.LOGGER.info(String.valueOf(id));
		DISCOVERED_TOOLS.add(new Tool(id, item));
		var a = item.getMaterial();
		for (ItemStack stack : a.getRepairIngredient().getMatchingStacks()) {
			Modulus.LOGGER.info(stack.toString());
		}
	}

	@Override
	public String toString() {
		return "ToolType{" +
			  "id=" + this.id +
			  ", pathName='" +
			  this.pathName + '\'' +
			  '}';
	}


}
