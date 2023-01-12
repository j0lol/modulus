package lol.j0.modulus.resource;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lol.j0.modulus.Modulus;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.mixin.AbstractBlockAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolType {
	public static final ToolType DIAMOND;
	private final Identifier id;
	private final String pathName;
	private static final List<ToolType> TYPES;

	private final Map<ComponentType, Component> components = new Reference2ObjectOpenHashMap<>();


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
			  ", pathName='" +
			  this.pathName + '\'' +
			  '}';
	}

	public static void onItemRegister(Identifier id, Item item) {
		Modulus.LOGGER.info(String.valueOf(id));

		for (var componentType : ComponentType.types()) {
			var toolName = componentType.filter(id, (ToolItem) item);
			if (toolName == null) continue;

			Identifier toolId;
			toolId = new Identifier(id.getNamespace(), toolName);


			var toolType = TYPES.stream().filter(type -> type.getId().equals(toolId)).findFirst()
					.orElseGet(() -> {
						var newToolType = new ToolType(toolId);
						TYPES.add(newToolType);
						return newToolType;
					});
			toolType.addComponent(componentType, new Component(toolType, (ToolItem) item));
			break;
		}

	//		Identifier toolId;
	//		toolId = new Identifier(id.getNamespace(), toolName);
	//
	//		var toolType = TYPES.stream().filter(type -> type.getId().equals(toolId)).findFirst()
	//				.orElseGet(() -> {
	//					var newToolType = new ToolType(toolId);
	//					TYPES.add(newToolType);
	//					return newToolType;
	//				});

	}
	private void addComponent(ComponentType type, Component component) {
		this.components.put(type, component);

		this.onToolTypeModified();
	}
	public record Component(ToolType toolType, ToolItem item) {
		public Identifier id() {return Registry.ITEM.getId(this.item.asItem());}
		public Ingredient repairIngredient() {
			return this.item.getMaterial().getRepairIngredient();
		}

		public Identifier texture() {
			var id = this.id();
			return new Identifier(id.getNamespace(), "item/" + id.getPath());
		}
	}
		// lambdafoxes why
	public enum ComponentType {
		PICKAXE(simpleFilter("pickaxe")),
		AXE(simpleFilter("axe")),
		SHOVEL(simpleFilter("shovel")),
		HOE(simpleFilter("hoe")),
		SWORD(simpleFilter("sword"));
		//BOW(simpleFilter("pickaxe")),
		//CROSSBOW(simpleFilter("pickaxe")),
		//TRIDENT(simpleFilter("pickaxe")),
		//FISHING_ROD(simpleFilter("pickaxe")),
		//SHIELD(simpleFilter("pickaxe"));
		private final Filter filter;
		private static final List<ComponentType> COMPONENT_TYPES = List.of(values());

		ComponentType(Filter filter) {
			this.filter = filter;
		}
		public @Nullable String filter(Identifier id, ToolItem toolItem) {
			return this.filter.filter(id, toolItem);
		}
		public static List<ComponentType> types() {
			return COMPONENT_TYPES;
		}

	}

	public interface Filter {
		@Nullable String filter(Identifier id, ToolItem toolItem);
	}
	private static Filter simpleFilter(String suffix) {
		return (id, item) -> {
			if (!id.getPath().endsWith('_' + suffix)) return null;
			return id.getPath().substring(0, id.getPath().length() - (suffix.length() + 1));
		};
	}

}
