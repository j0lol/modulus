package lol.j0.modulus.registry;

import lol.j0.modulus.api.Material;
import lol.j0.modulus.resource.ToolType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ToolTypeRegistry {

	static {
		TOOL_TYPES = new HashMap<>();
	}
	public static HashMap<Identifier, ToolType> TOOL_TYPES;

}
