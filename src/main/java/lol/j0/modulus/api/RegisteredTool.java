package lol.j0.modulus.api;

import lol.j0.modulus.registry.ToolRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class RegisteredTool {
	public Identifier identifier;
	public Item item;

	public RegisteredTool(Identifier identifier, Item item) {
		this.identifier = identifier;
		this.item = item;
	}

	public static void register(Identifier identifier, Item item) {
		ToolRegistry.TOOLS.put(identifier, new RegisteredTool(identifier, item));
	}

	public int getMiningLevel() {
		return 99;
	}
}
