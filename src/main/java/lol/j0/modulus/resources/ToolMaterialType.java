//package lol.j0.modulus.resources;
//
//import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
//import lol.j0.modulus.Modulus;
//import net.minecraft.util.Identifier;
//
//import javax.tools.Tool;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//
//public class ToolMaterialType {
//
//	public static final ToolMaterialType DIAMOND;
//	private static final List<ModificationCallbackEntry> CALLBACKS = new ArrayList<>();
//	private static final List<ToolMaterialType> TYPES;
//	private final Map<ComponentType, Component> components = new Reference2ObjectOpenHashMap<>();
//	private final List<ModificationCallbackEntry> toTrigger = new ArrayList<>();
//	private final Identifier id;
//	private final String pathName;
//	private final String absoluteLangPath;
//	private final String langPath;
//
//	static {
//		DIAMOND = new ToolMaterialType(new Identifier("diamond"));
//		TYPES = new ArrayList<>(List.of(DIAMOND));
//	}
//
//	public Identifier getId() {
//		return this.id;
//	}
//
//	private record ModificationCallbackEntry(Consumer<ToolMaterialType> callback, List<ComponentType> requiredComponents) {
//	}
//
//	public ToolMaterialType(Identifier id) {
//		this.id = id;
//		this.pathName = getPathName(this.id);
//		this.absoluteLangPath = this.pathName.replaceAll("/", ".");
//		this.langPath = getLangPath(this.id);
//
//		this.toTrigger.addAll(CALLBACKS);
//	}
//	public String getPathName() {
//		return this.pathName;
//	}
//	private static String getPathName(Identifier id) {
//		var path = id.getPath();
//		var namespace = id.getNamespace();
//		if (!namespace.equals("minecraft") && !namespace.equals(Modulus.MOD_ID))
//			path = namespace + '/' + path;
//		return path;
//	}
//	public String getAbsoluteLangPath() {
//		return this.absoluteLangPath;
//	}
//
//	public String getLangPath() {
//		return this.langPath;
//	}
//	private static String getLangPath(Identifier id) {
//		return switch (id.getPath()) {
//			case "azalea" -> "azalea"; // ???
//			case "bamboo" -> "bamboo";
//			case "redwood" -> "redwood";
//			default -> getPathName(id).replaceAll("/", ".");
//		};
//	}
//}
