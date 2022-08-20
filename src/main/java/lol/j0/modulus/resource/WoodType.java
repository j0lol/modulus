package lol.j0.modulus.resource;

import lol.j0.modulus.Modulus;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WoodType {

	public static final WoodType OAK;

	private static final List<WoodType> TYPES;
	private final Identifier id;
	private final String pathName;

	static {
		OAK = new WoodType(new Identifier("oak"));
		TYPES = new ArrayList<>(List.of(OAK));
	}

	public WoodType(Identifier id) {
		this.id = id;
		this.pathName = getPathName(this.id);
	}


	private static String getPathName(Identifier id) {
		var path = id.getPath();
		var namespace = id.getNamespace();
		if (!namespace.equals("minecraft") && !namespace.equals(Modulus.MOD_ID))
			path = namespace + '/' + path;
		return path;
	}

	public static @Nullable WoodType fromId(Identifier id) {
		for (var type : TYPES) {
			if (type.getId().equals(id))
				return type;
		}

		return null;
	}
	@Override
	public String toString() {
		return "WoodType{" +
				"id=" + this.id +
				", pathName='" + this.pathName + '\'' +
				'}';
	}
	public Identifier getId() {
		return this.id;
	}
	public String getName() {
		return this.id.toString().split(":")[1];
	}

}
