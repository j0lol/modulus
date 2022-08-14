//package lol.j0.modulus.client.render.model;
//
//import com.mojang.datafixers.util.Pair;
//import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
//import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
//import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.render.model.*;
//import net.minecraft.client.render.model.json.ModelOverrideList;
//import net.minecraft.client.render.model.json.ModelTransformation;
//import net.minecraft.client.texture.Sprite;
//import net.minecraft.client.texture.SpriteAtlasTexture;
//import net.minecraft.client.util.SpriteIdentifier;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.random.RandomGenerator;
//import net.minecraft.world.BlockRenderView;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//import static lol.j0.modulus.Modulus.LOGGER;
//import static lol.j0.modulus.Modulus.MOD_ID;
//
//public class ToolModel implements UnbakedModel, BakedModel, FabricBakedModel {
//
//	private static final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[]{
//			new SpriteIdentifier(new Identifier(MOD_ID, "atlas"), new Identifier("modulus:item/unfinished_modular_tool")),
//			new SpriteIdentifier(new Identifier(MOD_ID, "atlas"), new Identifier("modulus:item/tool_rod"))
//	};
//	private Sprite[] SPRITES = new Sprite[2];
//
//	ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
//		out.accept(modelId);
//	});
//BuiltinItemRendererRegistry.INSTANCE.register(MyMod.MY_ITEM, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
//		boolean left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;
//		BakedModel model = mc.getBakedModelManager().getModel(modelId);
//		mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
//	});
//
//
//
//
//
//	@Override
//	public boolean isVanillaAdapter() {
//		return false;
//	}
//
//	@Override
//	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
//
//	}
//
//	@Override
//	public void emitItemQuads(ItemStack stack, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
//
//		SpriteAtlasTexture a = SPRITES[0].getAtlas();
//		LOGGER.info(String.valueOf(a.getId()));
//
//
//	}
//
//	@Override
//	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomGenerator random) {
//		return null;
//	}
//
//	@Override
//	public boolean useAmbientOcclusion() {
//		return false;
//	}
//
//	@Override
//	public boolean hasDepth() {
//		return false;
//	}
//
//	@Override
//	public boolean isSideLit() {
//		return false;
//	}
//
//	@Override
//	public boolean isBuiltin() {
//		return false;
//	}
//
//	@Override
//	public Sprite getParticleSprite() {
//		return null;
//	}
//
//	@Override
//	public ModelTransformation getTransformation() {
//		return null;
//	}
//
//	@Override
//	public ModelOverrideList getOverrides() {
//		return null;
//	}
//
//	@Override
//	public Collection<Identifier> getModelDependencies() {
//		return Collections.emptyList(); // This model does not depend on other models.
//	}
//
//	@Override
//	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
//		return Arrays.asList(SPRITE_IDS); // The textures this model (and all its model dependencies, and their dependencies, etc...!) depends on.
//	}
//
//	@Nullable
//	@Override
//	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
//		return null;
//	}
//}
