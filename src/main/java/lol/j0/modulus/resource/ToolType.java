package lol.j0.modulus.resource;
import lol.j0.modulus.Modulus;
import net.minecraft.util.Identifier;

public class ToolType {
	public static final ToolType DIAMOND;
	private final Identifier id;
	private final String pathName;

	static {
		DIAMOND = new ToolType(new Identifier("diamond"));
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

}
