package lol.j0.modulus.resource;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lol.j0.modulus.Modulus;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.mixin.AbstractBlockAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolType {
	public static final ToolType DIAMOND;
	private final Identifier id;
	private final String pathName;
	public static final List<ToolType> TYPES;

	static {
		DIAMOND = new ToolType(new Identifier("diamond"));
		TYPES = new ArrayList<>(List.of(DIAMOND));
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

	public static void onItemRegister(Identifier id, Item item) {
		if (id.getPath().endsWith("_pickaxe")) {
			// add resource to LIST
			var resource = id.getPath().substring(0, id.getPath().length() - ("pickaxe".length() + 1));
			Modulus.LOGGER.info(resource);
			TYPES.add(new ToolType(new Identifier(resource)));
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
