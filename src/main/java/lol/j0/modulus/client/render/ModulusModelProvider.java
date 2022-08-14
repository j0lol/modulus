//package lol.j0.modulus.client.render;
//
//import lol.j0.modulus.client.render.model.ToolModel;
//import lol.j0.modulus.item.ModularToolItem;
//import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
//import net.fabricmc.fabric.api.client.model.ModelProviderContext;
//import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
//import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.UnbakedModel;
//import net.minecraft.client.render.model.json.ModelTransformation;
//import net.minecraft.util.Identifier;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Map;
//
//public class ModulusModelProvider implements ModelResourceProvider {
//
//	public static final Identifier TOOL_MODEL = new Identifier("modulus:item/cml_tool");
//
//	private static final Identifier ID = new Identifier("yttr", "builtin/cleaved_block");
//
//
//
//
//
//	@Override
//	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
//		if(resourceId.equals(TOOL_MODEL)) {
//			return new ToolModel();
//		} else {
//			return null;
//		}
//	}
//}
//
//
///*
//
//
//public class DynamicBlockModelProvider implements ModelResourceProvider {
//
//	private static final Map<Identifier, Class<? extends UnbakedModel>> IDS = Map.of(
//			Yttr.id("builtin/cleaved_block"), CleavedBlockModel.class,
//			Yttr.id("builtin/bloque"), BloqueModel.class
//		);
//
//	public static void init() {
//		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new DynamicBlockModelProvider());
//	}
//
//	@Override
//	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
//		if (IDS.containsKey(resourceId)) {
//			if (!RendererAccess.INSTANCE.hasRenderer()) {
//	 			YLog.warn("No implementation of the Fabric Rendering API was detected. Some blocks likely won't render, and may crash the game!");
//			}
//			try {
//				return IDS.get(resourceId).getConstructor().newInstance();
//			} catch (Exception e) {
//				throw new ModelProviderException("Failed to instance "+IDS.get(resourceId), e);
//			}
//		}
//		return null;
//	}
//
//}
//
// */
