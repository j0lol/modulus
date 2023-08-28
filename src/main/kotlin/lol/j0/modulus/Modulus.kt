package lol.j0.modulus

import ModularizerBlock
import lol.j0.modulus.api.HandleMaterial
import lol.j0.modulus.block.ModularizerBlockEntity
import lol.j0.modulus.gui.ModularizerGuiDescription
import lol.j0.modulus.item.ModularToolItem
import lol.j0.modulus.item.ModuleItem
import lol.j0.modulus.item.ToolHammerItem
import lol.j0.modulus.registry.ModulusRegistries
import lol.j0.modulus.resource.Datagen
import lol.j0.modulus.resource.ModulusPack
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.feature_flags.FeatureFlags
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceType
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import org.quiltmc.qsl.registry.api.event.RegistryEntryContext
import org.quiltmc.qsl.registry.api.event.RegistryMonitor
import org.quiltmc.qsl.resource.loader.api.ResourceLoader
import org.quiltmc.qsl.resource.loader.api.ResourcePackRegistrationContext
import org.slf4j.LoggerFactory


object Modulus : ModInitializer {


    lateinit var SCREEN_HANDLER_TYPE: ScreenHandlerType<*>

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod name as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public val LOGGER = LoggerFactory.getLogger("|MODULUS|")
    const val MOD_ID = "modulus"
    val RESOURCE_PACK = ModulusPack(ResourceType.SERVER_DATA)
    val MODULAR_TOOL = ModularToolItem(QuiltItemSettings().maxCount(1)) // todo use yttr submodules

    //val TOOL_ROD: Item = ToolRodItem(QuiltItemSettings().maxCount(64))
    val TOOL_HAMMER = ToolHammerItem(QuiltItemSettings().maxCount(1))
    val MODULE = ModuleItem(QuiltItemSettings().maxCount(1))
    lateinit var MODULARIZER_BLOCK_ENTITY: BlockEntityType<ModularizerBlockEntity>
    val MODULARIZER_BLOCK = ModularizerBlock(QuiltBlockSettings.create())

    val DEFAULT_HANDLE = HandleMaterial(durability = 100, repairIngredient = Ingredient.ofItems(Items.STICK))
    val NETHERITE_HANDLE = HandleMaterial(durability = 200, repairIngredient = Ingredient.ofItems(Items.STICK))

    //public static final ScreenHandlerType<ModularizerScreenHandler> MODULARIZER_SCREEN_HANDLER = new ScreenHandlerType<>((syncId, inventory) -> new ModularizerScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY));


    override fun onInitialize(mod: ModContainer?) {

        SCREEN_HANDLER_TYPE = Registry.register(
            Registries.SCREEN_HANDLER_TYPE, id("modularizer"),
            ScreenHandlerType(
                { syncId: Int, inventory: PlayerInventory? ->
                    ModularizerGuiDescription(
                        syncId,
                        inventory,
                        ScreenHandlerContext.EMPTY
                    )
                },
                FeatureFlags.VANILLA_SET
            )
        )


        //SerializationTest.test()
        RegistryMonitor.create(Registries.ITEM)
                .filter { context: RegistryEntryContext<Item?>? -> Datagen.filterItems(context!!) }
                .forAll { context: RegistryEntryContext<Item?>? -> Datagen.discoverItems(context) }

        Registry.register(Registries.BLOCK, id("modularizer"), MODULARIZER_BLOCK)
        Registry.register(Registries.ITEM, id("modularizer"), BlockItem(MODULARIZER_BLOCK, QuiltItemSettings()))
        Registry.register(Registries.ITEM, id("modular_tool"), MODULAR_TOOL)
        //Registry.register(Registries.ITEM, id("tool_rod"), TOOL_ROD)
        Registry.register(Registries.ITEM, id("tool_hammer"), TOOL_HAMMER)
        Registry.register(Registries.ITEM, id("module"), MODULE)

        MODULARIZER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id("modularizer"),
            QuiltBlockEntityTypeBuilder.create(::ModularizerBlockEntity, MODULARIZER_BLOCK).build(null)
        )
        ModulusRegistries.register(ModulusRegistries.HANDLE_MATERIALS, id("default_handle"), DEFAULT_HANDLE)
        ModulusRegistries.register(ModulusRegistries.HANDLE_MATERIALS, id("netherite_handle"), NETHERITE_HANDLE)

        ResourceLoader.get(ResourceType.SERVER_DATA).registerDefaultResourcePackEvent.register(
                ResourcePackRegistrationContext.Callback { context: ResourcePackRegistrationContext -> context.addResourcePack(RESOURCE_PACK.rebuild(ResourceType.SERVER_DATA, null)) }
        )
    }

    fun id(path: String?): Identifier {
        return Identifier(MOD_ID, path)
    }

}
