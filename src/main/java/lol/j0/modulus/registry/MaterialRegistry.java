package lol.j0.modulus.registry;

import lol.j0.modulus.api.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;

public class MaterialRegistry {

	static {
		MATERIALS = new HashMap<>();
	}
	public static HashMap<Identifier, Material> MATERIALS;
}
