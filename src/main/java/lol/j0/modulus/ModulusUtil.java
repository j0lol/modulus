package lol.j0.modulus;

import com.mojang.blaze3d.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

import static lol.j0.modulus.Modulus.LOGGER;

public class ModulusUtil {


	public static NativeImage itemToImage(Identifier identifier, ResourceManager resourceManager) {
		var texturePath = new Identifier(
			identifier.getNamespace(),
			"textures/item/" + identifier.getPath() + ".png"
		);
		var resource = resourceManager.getResource(texturePath);
		if (resource.isEmpty()) {
			LOGGER.error("Could not find texture for item " + identifier);
			return null;
		}

		try (InputStream is = resource.get().open()) {
			return NativeImage.read(is);
		} catch (IOException e) {
			LOGGER.error("Could not read texture for item " + identifier);
			return null;
		}
	}

	public static int average(int[] a) {
		int res=0;
		for (int n : a) res += n;
		return res/a.length;
	}

	public static Float average(Float[] a) {
		Float res=0f;
		for (Float n : a) res += n;
		return res/a.length;
	}
}
