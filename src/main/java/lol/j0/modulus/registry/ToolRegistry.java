package lol.j0.modulus.registry;

import lol.j0.modulus.api.RegisteredTool;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ToolRegistry {

	static {
		TOOLS = new HashMap<>();
	}
	public static HashMap<Identifier, RegisteredTool> TOOLS;

	public static void register(Identifier identifier, Item item) {
		ToolRegistry.TOOLS.put(identifier, new RegisteredTool(identifier, item));
	}
}
