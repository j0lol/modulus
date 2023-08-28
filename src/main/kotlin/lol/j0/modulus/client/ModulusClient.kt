package lol.j0.modulus.client

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.onFailure
import lol.j0.modulus.Modulus
import lol.j0.modulus.api.AssembledModularTool
import lol.j0.modulus.api.DisassembledModularTool
import lol.j0.modulus.api.ModulusDeserializeException
import lol.j0.modulus.api.Part
import lol.j0.modulus.gui.ModularizerBlockScreen
import lol.j0.modulus.gui.ModularizerGuiDescription
import lol.j0.modulus.item.ModularToolItem
import lol.j0.modulus.resource.Datagen
import lol.j0.modulus.resource.ModulusPack
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.resource.loader.api.ResourceLoader
import org.quiltmc.qsl.resource.loader.api.ResourcePackRegistrationContext
import java.util.function.Consumer


class ModulusClient : ClientModInitializer {
    internal inner class FunnyItemRenderer (
        private var stack: ItemStack, private var mode: ModelTransformationMode, private var matrices: MatrixStack,
        private var vertexConsumers: VertexConsumerProvider, private var light: Int, private var overlay: Int) {
        private var left: Boolean = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND

        // If you don't do this it renders wrong.
        init {
            matrices.pop()
            matrices.push()
        }

        fun render(bakedModel: BakedModel?) {
            ModelLoadingRegistry.INSTANCE.registerModelProvider { manager: ResourceManager?, out: Consumer<Identifier?> -> out.accept(model) }
            mc.itemRenderer.renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, bakedModel)
        }
    }

    override fun onInitializeClient(mod: ModContainer) {
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerDefaultResourcePackEvent.register(ResourcePackRegistrationContext.Callback { context: ResourcePackRegistrationContext -> context.addResourcePack(RESOURCE_PACK.rebuild(ResourceType.CLIENT_RESOURCES, context.resourceManager())) })


        // FIXME: peek at libgui 1.20 (falk source?)
//        HandledScreens.register(Modulus.SCREEN_HANDLER_TYPE,
//            HandledScreens.Provider { screenHandler, playerInventory, text -> ScreenHandlerProvider<ModularizerBlockScreen>(
//
//            ) }
//            )


        // Register models, otherwise they won't render when called
        ModelLoadingRegistry.INSTANCE.registerModelProvider { _: ResourceManager?, out: Consumer<Identifier?> ->
            out.accept(TOOL_EDIT_SELECTION)
            out.accept(TOOL_ROD)
            out.accept(HOLOGRAM)
            out.accept(DEFAULT_HANDLE)
            out.accept(DEFAULT_HANDLE_BROKEN)
            out.accept(NETHERITE_HANDLE)
            for (modelIdentifier in Datagen.CREATED_MODELS!!) {
                out.accept(modelIdentifier)
            }
        }

        BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULE) { stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int ->
            val renderer = FunnyItemRenderer(stack, mode, matrices, vertexConsumers, light, overlay)

            val model = when (val part = Part.deserialize(stack.orCreateNbt)) {
                is Success -> part.get().getModelID()
                is Failure -> HOLOGRAM
            }
            renderer.render(mc.bakedModelManager.getModel(model))
        }
        BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL) { stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int ->
            val renderer = FunnyItemRenderer(stack, mode, matrices, vertexConsumers, light, overlay)

            try {
                val parts = if (ModularToolItem.getIfEditable(stack)) {
                    val tool = DisassembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }
                    if (mode == ModelTransformationMode.GUI) {
                        renderer.render(mc.bakedModelManager.getModel(TOOL_EDIT_SELECTION))
                    }
                    tool.parts.filterNotNull().sortedBy { part: Part -> part.zIndex }
                } else {
                    val tool: AssembledModularTool = AssembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }
                    tool.parts.sortedBy { part: Part -> part.zIndex }
                }

                for (part in parts) {
                    renderer.render(mc.bakedModelManager.getModel(part.getModelID()))
                }
            }
            catch (exception: ModulusDeserializeException) {
                renderer.render(mc.bakedModelManager.getModel(HOLOGRAM))
            }

        }
    }

    companion object {
        // Get the vanilla models
        val TOOL_EDIT_SELECTION = ModelIdentifier(Modulus.id("unfinished_modular_tool"), "inventory")
        val TOOL_ROD = ModelIdentifier(Modulus.id("tool_rod"), "inventory")
        val DEFAULT_HANDLE = ModelIdentifier(Modulus.id("default_handle"), "inventory")
        val DEFAULT_HANDLE_BROKEN = ModelIdentifier(Modulus.id("default_handle_broken"), "inventory")
        val NETHERITE_HANDLE = ModelIdentifier(Modulus.id("netherite_handle"), "inventory")
        val HOLOGRAM = ModelIdentifier(Modulus.id("hologram"), "inventory")
        val model = ModelIdentifier(Modulus.id("birch_tool_rod"), "inventory")
        private val mc = MinecraftClient.getInstance()
        val RESOURCE_PACK = ModulusPack(ResourceType.CLIENT_RESOURCES)
    }
}
