package lol.j0.modulus.resource;

import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;

public class Tool {
	public Identifier identifier;
	public ToolItem item;

	Tool(Identifier identifier, ToolItem item) {
		this.identifier = identifier;
		this.item = item;
	}
}
