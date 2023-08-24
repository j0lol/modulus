package lol.j0.modulus.resource

import com.mojang.blaze3d.texture.NativeImage
import lol.j0.modulus.Modulus
import lol.j0.modulus.api.RegisteredTool
import lol.j0.modulus.registry.ToolTypeRegistry
import net.minecraft.resource.ResourceType
import org.quiltmc.qsl.resource.loader.api.InMemoryResourcePack
import java.io.IOException

class ModulusPack (private val type: ResourceType) : InMemoryResourcePack() {
    fun rebuild(type: ResourceType, resourceManager: net.minecraft.resource.ResourceManager?): ModulusPack {

        // register tags here. or not who cares. im not making items here anyway.
        registerTag(
                arrayOf("items"),
                Modulus.id("valid_tools"),
                Datagen.DISCOVERED_TOOLS?.stream()!!.map { tool: Datagen.DiscoveredTool -> tool.identifier })
        return if (type == ResourceType.CLIENT_RESOURCES) rebuildClient(resourceManager) else rebuildData()
    }
    private fun rebuildData(): ModulusPack {
        // calls to Datagen to register each item
        return this
    }
    private fun rebuildClient(resourceManager: net.minecraft.resource.ResourceManager?): ModulusPack {
        Datagen.generateClientData(resourceManager!!)
        return this
    }
    private fun registerTag(types: kotlin.Array<String>, id: net.minecraft.util.Identifier, entries: java.util.stream.Stream<net.minecraft.util.Identifier>) {
        val root = com.google.gson.JsonObject()
        root.addProperty("replace", false)
        val values = com.google.gson.JsonArray()
        entries.forEach { value: net.minecraft.util.Identifier -> values.add(value.toString()) }
        root.add("values", values)
        for (type in types)  {
            putJson(ResourceType.SERVER_DATA, net.minecraft.util.Identifier(id.namespace, "tags/" + type + "/" + id.path), root)
        }
    }
    fun putJsonText(type: ResourceType?, id: net.minecraft.util.Identifier, json: String?) {
        this.putText(type!!, net.minecraft.util.Identifier(id.namespace, id.path + ".json"), json!!)
    }
    fun putJson(type: ResourceType?, id: net.minecraft.util.Identifier, json: com.google.gson.JsonObject?) {
        var id = id
        if (!id.path.endsWith(".json"))id = net.minecraft.util.Identifier(id.namespace, id.path + ".json")
        val stringWriter = java.io.StringWriter()
        val jsonWriter = com.google.gson.stream.JsonWriter(stringWriter)
        jsonWriter.isLenient = true
        jsonWriter.setIndent("  ")
        try  {
            com.google.gson.internal.Streams.write(json, jsonWriter)
        }catch (e: IOException) {
            LOGGER.error("Failed to write JSON at {}.", id, e)
        }
        this.putText(type!!, id, stringWriter.toString())
    }
    override fun putImage(id: net.minecraft.util.Identifier, image: NativeImage) {
        var id = id
        if (!id.path.endsWith(".png"))id = net.minecraft.util.Identifier(id.namespace, "textures/" + id.path + ".png")
        try  {
            super.putImage(id, image)
        }catch (e: IOException) {
            LOGGER.warn("Could not close output channel for texture $id.", e)
        }
    }
    override fun getName(): String {
        return "Modulus Virtual Pack"
    }
    companion object {
        private val LOGGER = org.slf4j.LoggerFactory.getLogger("Modulus Datagen")
    }
}
