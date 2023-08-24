package lol.j0.modulus.resource

import com.google.gson.JsonObject
import com.mojang.blaze3d.texture.NativeImage
import lol.j0.modulus.ColorUtil
import lol.j0.modulus.ImageLibs.itemToImage
import lol.j0.modulus.Modulus
import lol.j0.modulus.client.ModulusClient
import lol.j0.modulus.registry.ToolRegistry
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Item
import net.minecraft.item.ShovelItem
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import org.quiltmc.qsl.registry.api.event.RegistryEntryContext
import uk.co.samwho.result.Result

class Datagen {
    class DiscoveredTool internal constructor(var identifier: Identifier, var item: ToolItem)

    private val json = JsonObject()

    companion object {
        var CREATED_MODELS: ArrayList<ModelIdentifier>? = null
        var DISCOVERED_TOOLS: ArrayList<DiscoveredTool>? = null

        init {
            CREATED_MODELS = ArrayList()
            DISCOVERED_TOOLS = ArrayList()
        }

        fun filterItems(context: RegistryEntryContext<Item?>): Boolean {
            return context.value() is ToolItem
        }

        fun discoverItems(context: RegistryEntryContext<Item?>?) {
            if (context != null) {
                DISCOVERED_TOOLS!!.add(DiscoveredTool(context.id(), context.value() as ToolItem))
            }
        }

        fun generateClientData(resourceManager: ResourceManager) {
            try {
                generateModuleClientData(resourceManager)
            } catch (e: Exception) {
                Modulus.LOGGER.info("Generating module client data failed.")
            }
        }

        @Throws(Exception::class)
        private fun generateModuleClientData(resourceManager: ResourceManager) {

            // naive method of texture generation: palette filter the stick texture
            val stickImage = itemToImage(Identifier("minecraft", "stick"), resourceManager)?.getOrThrow()
                    ?: return
            val stickPalette = ColorUtil.getPaletteFromImage(stickImage)

            // loop through all resources: generate each tool.
            // ASSUMPTION CHECK: does each resource have a pick,axe,hoe,sword,shovel?
            //   should this be put somewhere? this should be done in reg-al
            //   idea: ToolType contains each ToolItem relevant, so shenanigans can ensue
            Modulus.LOGGER.info("Starting resource generation...")
            for (tool in DISCOVERED_TOOLS!!) {
                val image = itemToImage(tool.identifier, resourceManager)?.getOrThrow() ?: return

                // find invalid tools: huge textures, no repair ingredients
                if (tool.item.material.repairIngredient.matchingStacks.isEmpty() || image.height != 16 || image.width != 16) {
                    Modulus.LOGGER.info("Nonstandard tool. Giving up on " + tool.identifier)
                    //continue;
                }

                // i don't want to support swords right now.
                if (tool.item is SwordItem || tool.item is ShovelItem) {
                    Modulus.LOGGER.info("Unsupported tool type. Giving up on " + tool.identifier)
                    continue
                }
                val splitImage = DatagenUtils.imageSplitter(maskImage(image, tool, resourceManager)
                        .get()) { x: Double -> image.height - x }
                val newImageLeft = splitImage.getOrThrow().left
                val newImageRight = splitImage.getOrThrow().right
                val textureIdLeft = Modulus.id("item/" + DatagenUtils.makeModuleIdString(tool.identifier, "a"))
                val textureIdRight = Modulus.id("item/" + DatagenUtils.makeModuleIdString(tool.identifier, "b"))

                // build & put models
                DatagenUtils.modelBuilder(Identifier("item/handheld"))
                        .texture("layer0", textureIdLeft)
                        .register(textureIdLeft)
                DatagenUtils.modelBuilder(Identifier("item/handheld"))
                        .texture("layer0", textureIdRight)
                        .register(textureIdRight)
                CREATED_MODELS!!.add(ModelIdentifier(Modulus.id(DatagenUtils.makeModuleIdString(tool.identifier, "a")), "inventory"))
                CREATED_MODELS!!.add(ModelIdentifier(Modulus.id(DatagenUtils.makeModuleIdString(tool.identifier, "b")), "inventory"))

                // add item to valid_tools tag
                // ModulusClient.RESOURCE_PACK.open(ResourceType.CLIENT_RESOURCES, ) ??

                // put image
                ModulusClient.RESOURCE_PACK.putImage(textureIdLeft, newImageLeft)
                ModulusClient.RESOURCE_PACK.putImage(textureIdRight, newImageRight)

                // add tool to "registry"
                ToolRegistry.register(tool.identifier, tool.item)
            }
        }

        private fun maskImage(target: NativeImage, tool: DiscoveredTool, resourceManager: ResourceManager): Result<NativeImage> {
            val stickImage = itemToImage(Identifier("minecraft", "stick"), resourceManager)?.get()
            val stickPalette = ColorUtil.getPaletteFromImage(stickImage!!)
            if (tool.identifier.namespace == "minecraft") {
                return methodB(target, tool, resourceManager)
            }
            Modulus.LOGGER.info(stickPalette.toString())
            return if (ColorUtil.getPaletteFromImage(target).intStream().anyMatch { i: Int -> stickPalette.contains(i) }) {
                Modulus.LOGGER.info("Method A on " + tool.identifier)
                methodA(target, resourceManager)
            } else {
                Modulus.LOGGER.info("Method B on " + tool.identifier)
                methodB(target, tool, resourceManager)
            }
        }

        private fun methodA(target: NativeImage, resourceManager: ResourceManager): Result<NativeImage> {
            val stickImage = itemToImage(Identifier("minecraft", "stick"), resourceManager)?.get()
            return DatagenUtils.paletteMask(target, stickImage)
        }

        private fun methodB(target: NativeImage, tool: DiscoveredTool, resourceManager: ResourceManager): Result<NativeImage> {
            val lastIndex = tool.identifier.path.lastIndexOf("_")
            val toolType = tool.identifier.path.substring(lastIndex + 1)
            val diamondTool = itemToImage(Identifier("minecraft", "diamond_$toolType"), resourceManager)?.get()
            val diamondHead = methodA(diamondTool!!, resourceManager).get()
            return Result.success(DatagenUtils.imageInvertedMask(target, diamondHead))
        }
    }
}
