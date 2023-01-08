package lol.j0.modulus.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.mojang.blaze3d.texture.NativeImage;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.item.ModuleItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import com.google.gson.stream.JsonWriter;
import org.quiltmc.qsl.resource.loader.api.InMemoryResourcePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

public class ModulusPack extends InMemoryResourcePack {
	private static final Logger LOGGER = LoggerFactory.getLogger("Modulus Datagen");;
	private final ResourceType type;

	public ModulusPack(ResourceType type) {
		this.type = type;
	}

	public ModulusPack rebuild(ResourceType type, @Nullable ResourceManager resourceManager) {
		this.registerTag(new String[]{"items"}, Modulus.id("shelves"), ModuleItem.streamModules()
				.map(Registry.ITEM::getId));

		// stuff here

		return type == ResourceType.CLIENT_RESOURCES ? this.rebuildClient(resourceManager) : this.rebuildData();
	}

	private ModulusPack rebuildData() {
		// calls to Datagen to register each item
		return this;
	}

	private ModulusPack rebuildClient(ResourceManager resourceManager) {
		return this;
	}

	private void registerTag(String[] types, Identifier id, Stream<Identifier> entries) {
		var root = new JsonObject();
		root.addProperty("replace", false);
		var values = new JsonArray();

		entries.forEach(value -> values.add(value.toString()));

		root.add("values", values);

		for (var type : types) {
			this.putJson(ResourceType.SERVER_DATA, new Identifier(id.getNamespace(), "tags/" + type + "/" + id.getPath()), root);
		}
	}

	public void putJsonText(ResourceType type, Identifier id, String json) {
		this.putText(type, new Identifier(id.getNamespace(), id.getPath() + ".json"), json);
	}

	public void putJson(ResourceType type, Identifier id, JsonObject json) {
		if (!id.getPath().endsWith(".json")) id = new Identifier(id.getNamespace(), id.getPath() + ".json");

		var stringWriter = new StringWriter();
		var jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setLenient(true);
		jsonWriter.setIndent("  ");
		try {
			Streams.write(json, jsonWriter);
		} catch (IOException e) {
			LOGGER.error("Failed to write JSON at {}.", id, e);
		}

		this.putText(type, id, stringWriter.toString());
	}

	public void putImage(Identifier id, NativeImage image) {
		if (!id.getPath().endsWith(".png")) id = new Identifier(id.getNamespace(), "textures/" + id.getPath() + ".png");
		try {
			super.putImage(id, image);
		} catch (IOException e) {
			LOGGER.warn("Could not close output channel for texture " + id + ".", e);
		}
	}
	@Override
	public String getName() {
		return "Modulus Virtual Pack";
	}
}
