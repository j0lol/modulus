package lol.j0.modulus.client

import lol.j0.modulus.Modulus
import lol.j0.modulus.api.AssembledModularTool
import lol.j0.modulus.api.DeserializeException
import lol.j0.modulus.api.DisassembledModularTool
import lol.j0.modulus.api.Part
import lol.j0.modulus.item.ModularToolItem
import lol.j0.modulus.resource.Datagen
import lol.j0.modulus.resource.ModulusPack
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.resource.loader.api.ResourceLoader
import org.quiltmc.qsl.resource.loader.api.ResourcePackRegistrationContext
import java.util.function.Consumer

/// All I want to do is layer sprites.... HOWEVER! I need to do this based on the nbt of the item
class ModulusClient : ClientModInitializer {
//    var ModuleModels = Hashtable<String, ModelIdentifier>()
//    var RodModels = Hashtable<String, ModelIdentifier>()

    internal inner class FunnyRenderingStuff(var stack: ItemStack, var mode: ModelTransformationMode, var matrices: MatrixStack,
                                             var vertexConsumers: VertexConsumerProvider, var light: Int, var overlay: Int) {
        private var left: Boolean = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND

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

        /* this needs to be simplified */
//        val list = guessModelNames()
//        val toolRodList = guessToolRodModelNames()
//        for (i in list) {
//            ModuleModels[i] = ModelIdentifier(Modulus.id(i), "inventory")
//        }
//        for (i in toolRodList) {
//            RodModels[i] = ModelIdentifier(Modulus.id(i), "inventory")
//        }

        ModelLoadingRegistry.INSTANCE.registerModelProvider { manager: ResourceManager?, out: Consumer<Identifier?> ->
            out.accept(TOOL_EDIT_SELECTION)
            out.accept(TOOL_ROD)
            out.accept(HOLOGRAM)
            for (modelIdentifier in Datagen.CREATED_MODELS!!) {
                out.accept(modelIdentifier)
            }
        }

        BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULE) { stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int ->
            val renderer = FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay)

            val model = Part.deserialize(stack.orCreateNbt)?.getModelID()
            renderer.render(mc.bakedModelManager.getModel(model))
        }
        BuiltinItemRendererRegistry.INSTANCE.register(Modulus.TOOL_ROD) { stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int ->
            val renderer = FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay)
            renderer.render(mc.bakedModelManager.getModel(TOOL_ROD))
        }
        BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL) { stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int ->
            val renderer = FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay)

            try {
                val parts = if (ModularToolItem.getIfEditable(stack)) {
                    val tool = DisassembledModularTool.deserialize(stack.orCreateNbt)
                    if (mode == ModelTransformationMode.GUI) {
                        renderer.render(mc.bakedModelManager.getModel(TOOL_EDIT_SELECTION))
                    }
                    tool.parts.filterNotNull().sortedBy { part: Part -> part.zIndex }
                } else {
                    val tool = AssembledModularTool.deserialize(stack.orCreateNbt)
                    tool?.parts?.sortedBy { part: Part -> part.zIndex }
                }

                if (parts != null) {
                    for (part in parts) {
                        renderer.render(mc.bakedModelManager.getModel(part.getModelID()))
                    }
                }
            } catch (exception: DeserializeException) {
                renderer.render(mc.bakedModelManager.getModel(HOLOGRAM))
            }

        }
    }

//    private fun guessModelNames(): ArrayList<String> {
//        val resources = arrayOf("wooden", "stone", "iron", "golden", "diamond", "netherite")
//        val types = arrayOf("axe", "hoe", "pickaxe", "sword", "shovel")
//        val out = ArrayList<String>()
//        for (r in resources) {
//            for (t in types) {
//                out.add("module_" + r + "_" + t + "_a")
//                out.add("module_" + r + "_" + t + "_b")
//                if (t == "axe" || t == "hoe") {
//                    out.add("flipped_module_" + r + "_" + t + "_a")
//                    out.add("flipped_module_" + r + "_" + t + "_b")
//                }
//            }
//        }
//        return out
//    }
//
//    private fun guessToolRodModelNames(): ArrayList<String> {
//        val resources = arrayOf("vanilla", "acacia", "birch", "crimson", "dark_oak", "jungle", "mangrove", "oak", "spruce", "warped")
//        val out = ArrayList<String>()
//        for (r in resources) {
//            out.add(r + "_tool_rod")
//        }
//        return out
//    }

    public companion object {
        // Get the vanilla models
        val TOOL_EDIT_SELECTION = ModelIdentifier(Modulus.id("unfinished_modular_tool"), "inventory")
        val TOOL_ROD = ModelIdentifier(Modulus.id("tool_rod"), "inventory")
        val HOLOGRAM = ModelIdentifier(Modulus.id("hologram"), "inventory")
        val model = ModelIdentifier(Modulus.id("birch_tool_rod"), "inventory")
        private val mc = MinecraftClient.getInstance()
        val RESOURCE_PACK = ModulusPack(ResourceType.CLIENT_RESOURCES)
    }
}
