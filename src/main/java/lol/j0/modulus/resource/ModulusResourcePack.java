package lol.j0.modulus.resource;

import com.mojang.blaze3d.texture.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lol.j0.modulus.Modulus;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.resource.loader.impl.ModResourcePackUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModulusResourcePack implements ResourcePack {

	private final Set<String> namespaces = new HashSet<>();
	private final Map<String, byte[]> resources = new Object2ObjectOpenHashMap<>();
	private final ResourceType type;

	public ModulusResourcePack(ResourceType type) {
		this.type = type;
	}
	@Nullable
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		var metadata = QuiltLoader.getModContainer(Modulus.MOD_ID).get().metadata();

		if (ModResourcePackUtil.containsDefault(metadata, fileName)) {
			return ModResourcePackUtil.openDefault(metadata,
					this.type,
					fileName);
		}

		byte[] data;
		if ((data = this.resources.get(fileName)) != null) {
			return new ByteArrayInputStream(data);
		}
		throw new IOException("Generated resources pack has no data or alias for " + fileName);
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		return this.openRoot(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix,
												Predicate<Identifier> pathFilter) {
		var start = type.getDirectory() + "/" + namespace + "/" + prefix;
		return this.resources.keySet().stream()
				.filter(s -> s.startsWith(start))
				.map(ModulusResourcePack::fromPath)
				.filter(pathFilter)
				.collect(Collectors.toList());
	}


	@Override
	public boolean contains(ResourceType type, Identifier id) {
		var path = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
		return this.resources.containsKey(path);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.namespaces;
	}


	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return "Modulus Virtual Pack";
	}


	@Override
	public void close() {
		if (this.type == ResourceType.CLIENT_RESOURCES) {
			this.resources.clear();
			this.namespaces.clear();
		}
	}

	private static Identifier fromPath(String path) {
		String[] split = path.replaceAll("((assets)|(data))/", "").split("/", 2);

		return new Identifier(split[0], split[1]);
	}



}
