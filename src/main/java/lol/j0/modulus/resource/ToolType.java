package lol.j0.modulus.resource;
import lol.j0.modulus.Modulus;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ToolType {
	public static final ToolType DIAMOND;
	private final Identifier id;
	private final String pathName;
	private static final List<ToolType> TYPES;


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

	@Override
	public String toString() {
		return "ToolType{" +
				"id=" + this.id +
				", pathName='" + this.pathName + '\'' +
				'}';
	}
	public static void onItemRegister(Identifier id, Item item) {
		var toolName = componentType.filter(id, item);
		if (toolName == null) continue;

		Identifier toolId;
		toolId = new Identifier(id.getNamespace(), toolName);

		var toolType = TYPES.stream().filter(type -> type.getId().equals(toolId)).findFirst()
				.orElseGet(() -> {
					var newToolType = new ToolType(toolId);
					TYPES.add(newToolType);
					return newToolType;
				});

	}

}
