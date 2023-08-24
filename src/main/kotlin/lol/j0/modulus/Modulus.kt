package lol.j0.modulus

import Modularizer
import lol.j0.modulus.item.ModularToolItem
import lol.j0.modulus.item.ModuleItem
import lol.j0.modulus.item.ToolHammerItem
import lol.j0.modulus.item.ToolRodItem
import lol.j0.modulus.resource.Datagen
import lol.j0.modulus.resource.ModulusPack
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import org.quiltmc.qsl.registry.api.event.RegistryEntryContext
import org.quiltmc.qsl.registry.api.event.RegistryMonitor
import org.quiltmc.qsl.resource.loader.api.ResourceLoader
import org.quiltmc.qsl.resource.loader.api.ResourcePackRegistrationContext
import org.slf4j.LoggerFactory

object Modulus : ModInitializer {


    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod name as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public val LOGGER = LoggerFactory.getLogger("|MODULUS|")
    const val MOD_ID = "modulus"
    val RESOURCE_PACK = ModulusPack(ResourceType.SERVER_DATA)
    val MODULAR_TOOL = ModularToolItem(QuiltItemSettings().maxCount(1)) // todo use yttr submodules

    val TOOL_ROD: Item = ToolRodItem(QuiltItemSettings().maxCount(64))
    val TOOL_HAMMER = ToolHammerItem(QuiltItemSettings().maxCount(1))
    val MODULE = ModuleItem(QuiltItemSettings().maxCount(1))
    val MODULARIZER = Modularizer(QuiltBlockSettings.create())
    //public static final ScreenHandlerType<ModularizerScreenHandler> MODULARIZER_SCREEN_HANDLER = new ScreenHandlerType<>((syncId, inventory) -> new ModularizerScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY));


    override fun onInitialize(mod: ModContainer?) {
        RegistryMonitor.create(Registries.ITEM)
                .filter { context: RegistryEntryContext<Item?>? -> Datagen.filterItems(context!!) }
                .forAll { context: RegistryEntryContext<Item?>? -> Datagen.discoverItems(context) }

        Registry.register(Registries.BLOCK, id("modularizer"), MODULARIZER)
        Registry.register(Registries.ITEM, id("modularizer"), BlockItem(MODULARIZER, QuiltItemSettings()))
        Registry.register(Registries.ITEM, id("modular_tool"), MODULAR_TOOL)
        Registry.register(Registries.ITEM, id("tool_rod"), TOOL_ROD)
        Registry.register(Registries.ITEM, id("tool_hammer"), TOOL_HAMMER)
        Registry.register(Registries.ITEM, id("module"), MODULE)

        //Registry.register(Registry.SCREEN_HANDLER, Modulus.id("modularizer"), MODULARIZER_SCREEN_HANDLER);


        //Registry.register(Registry.SCREEN_HANDLER, Modulus.id("modularizer"), MODULARIZER_SCREEN_HANDLER);
        ResourceLoader.get(ResourceType.SERVER_DATA).registerDefaultResourcePackEvent.register(
                ResourcePackRegistrationContext.Callback { context: ResourcePackRegistrationContext -> context.addResourcePack(RESOURCE_PACK.rebuild(ResourceType.SERVER_DATA, null)) }
        )
    }

    fun id(path: String?): Identifier {
        return Identifier(MOD_ID, path)
    }

}
