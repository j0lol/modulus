package lol.j0.modulus.resource;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.api.RegisteredTool;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ToolType {
	private final Identifier id;
	private final String pathName;
	public static final List<ToolType> TYPES;
	public static final List<RegisteredTool> DISCOVERED_TOOLS;

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

//	public static void onItemRegister(Identifier id, ToolItem item) {
//		Modulus.LOGGER.info(String.valueOf(id));
//		DISCOVERED_TOOLS.add(new RegisteredTool(id, item));
//		var a = item.getMaterial();
//		for (ItemStack stack : a.getRepairIngredient().getMatchingStacks()) {
//			Modulus.LOGGER.info(stack.toString());
//		}
//	}

	@Override
	public String toString() {
		return "ToolType{" +
			  "id=" + this.id +
			  ", pathName='" +
			  this.pathName + '\'' +
			  '}';
	}


}
