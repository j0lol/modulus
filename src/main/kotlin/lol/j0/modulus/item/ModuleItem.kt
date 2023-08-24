package lol.j0.modulus.item

import lol.j0.modulus.Modulus.id
import lol.j0.modulus.resource.DatagenUtils
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterial
import net.minecraft.util.Identifier
import java.util.stream.Stream

class ModuleItem : Item {
    var ModuleSide: MODULE_SIDE? = null

    enum class MODULE_SIDE {
        A,

        // Left, Down
        B // Right, Up
    }

    constructor(side: MODULE_SIDE?, material: ToolMaterial?, settings: Settings?) : super(settings) {
        ModuleSide = side
        MODULES.add(this)
    }

    constructor(settings: Settings?) : super(settings)

    companion object {
        private val MODULES: MutableList<ModuleItem> = ArrayList()
        fun streamModules(): Stream<ModuleItem> {
            return MODULES.stream()
        }

        // todo: module speed. module enchantability. module mining level (Take ItemStacks)
        //	public static ToolMaterials getMaterial(ItemStack stack) {
        //		return ToolMaterials.fromString(stack.getOrCreateNbt().getString("material"));
        //	}
        fun getType(stack: ItemStack): String {
            return stack.getOrCreateNbt().getString("type")
        }

        fun getNbt(stack: ItemStack, id: String): String {
            return stack.getOrCreateNbt().getString("modulus:$id")
        }

        fun setNbt(stack: ItemStack, id: String, value: String?) {
            stack.getOrCreateNbt().putString("modulus:$id", value)
        }

        //	public static int getMiningLevel(ItemStack stack) {
        //		return ToolMaterials.fromString(stack.getOrCreateNbt().getString("material")).miningLevel;
        //	}
        fun getModelID(stack: ItemStack): ModelIdentifier {
            if (stack.nbt == null) {
                return ModelIdentifier(id("hologram"), "inventory")
            }

//		var t = getType(stack).getModelName();
//		var m = getMaterial(stack).getModelName();
//		var name = stack.getOrCreateNbt().getString("tool_name");
//		var side = stack.getOrCreateNbt().getString("side");
            var string: String? = ""

//		if ((name.contains("axe") && !name.contains("pickaxe")) || name.contains("hoe")) {
//			if (t.equals("butt")) {
//				if (Objects.equals(side, "b")) {side="a";} else {string += "flipped_";}
//				string += "module_" + m + "_" + name.split("_")[1] + "_" + side;
//				return string.toLowerCase();
//			} else {
//				if (Objects.equals(side, "a")) {side = "b";} else {string += "flipped_";}
//				string += "module_" + m + "_" + t + "_" + side;
//				return string.toLowerCase();
//			}
//		} else {
            string = DatagenUtils.makeModuleIdString(Identifier(getNbt(stack, "identifier")), getNbt(stack, "side"))
            //Modulus.LOGGER.info(string);
            //return new ModelIdentifier(Modulus.id("hologram"), "inventory");
            return ModelIdentifier(id(string), "inventory")
            //		}
        }
    }
}
