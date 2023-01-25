package lol.j0.modulus;

import com.mojang.blaze3d.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import uk.co.samwho.result.Result;

import java.io.IOException;
import java.io.InputStream;

import static lol.j0.modulus.Modulus.LOGGER;

public class ModulusUtil {


	public static Result<NativeImage> itemToImage(Identifier identifier, ResourceManager resourceManager) {
		var texturePath = new Identifier(
			identifier.getNamespace(),
			"textures/item/" + identifier.getPath() + ".png"
		);
		var resource = resourceManager.getResource(texturePath);
		if (resource.isEmpty()) {
			return Result.fail(new Exception("Could not find texture for item " + identifier));
		}

		try (InputStream is = resource.get().open()) {
			return Result.success(NativeImage.read(is));
		} catch (IOException e) {
			return Result.fail(new Exception("Could not read texture for item " + identifier));
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
