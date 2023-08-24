package lol.j0.modulus.item

import lol.j0.modulus.Modulus
import lol.j0.modulus.api.*
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.StackReference
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.Slot
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ClickType
import net.minecraft.util.math.MathHelper
import kotlin.math.max

class ModularToolItem(settings: Settings?) : Item(settings) {
    private fun damage(stack: ItemStack, amount: Int, wielder: LivingEntity) {
        if (!getIfEditable(stack) && !wielder.world.isClient && (wielder !is PlayerEntity || !wielder.abilities.creativeMode)) {
            stack.getOrCreateNbt().putInt("Damage", stack.getOrCreateNbt().getInt("Damage") + amount)
            if (stack.getOrCreateNbt().getInt("Damage") >= (getDurability(stack) ?: 0)) {
                if (wielder is PlayerEntity) {
                    wielder.playSound(SoundEvents.ENTITY_ITEM_BREAK)
                    toggleIfEditable(stack, wielder)
                } else {
                    Modulus.LOGGER.info("test break")
                    //wielder.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    wielder.playSound(SoundEvents.ENTITY_ITEM_BREAK)
                }
            }
        }
    }

    override fun getEnchantability(): Int {
        Modulus.LOGGER.info("sorry this is not complete yet")
        return 30
    }

//    private fun getEffectiveBlocks(stack: ItemStack): Array<TagKey<*>?> {
//        val tagListA: Array<TagKey<*>?> = arrayOfNulls(2)
//        var pointer = 0
//        val list = getModuleList(stack)
//        for (module in list) {
//            val item = ItemStack.fromNbt(module as NbtCompound)
//            if (item.nbt != null) {
//            }
//            if (item.nbt != null && item.nbt!!.getString("type") == "pickaxe") {
//                tagListA[pointer] = BlockTags.PICKAXE_MINEABLE
//                pointer++
//            } else if (item.nbt != null && item.nbt!!.getString("type") == "axe") {
//                tagListA[pointer] = BlockTags.AXE_MINEABLE
//                pointer++
//            } else if (item.nbt != null && item.nbt!!.getString("type") == "shovel") {
//                tagListA[pointer] = BlockTags.SHOVEL_MINEABLE
//                pointer++
//            } else if (item.nbt != null && item.nbt!!.getString("type") == "hoe") {
//                tagListA[pointer] = BlockTags.HOE_MINEABLE
//                pointer++
//            }
//        }
//        for (i in tagListA.indices) {
//            val x = tagListA[i]
//            if (x == null) {
//                tagListA[i] = BlockTags.FIRE
//            }
//        }
//        return tagListA
//    }

    override fun onClicked(stack: ItemStack, otherStack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity, cursorStackReference: StackReference): Boolean {
        if (clickType == ClickType.RIGHT && stack.nbt != null && stack.nbt!!.getBoolean("modulus:is_editable")) {
            if (otherStack.isEmpty) {
                return removeModule(stack, player, cursorStackReference)
            }
            run { return addModule(stack, otherStack, player, cursorStackReference) }
        }
        return false
    }

    private fun addModule(stack: ItemStack, module: ItemStack, player: PlayerEntity, cursor: StackReference): Boolean {

        if (getIfEditable(stack)) {
            val modularTool = DisassembledModularTool.deserialize(stack.orCreateNbt)
            return when (modularTool.addPart(Part.deserialize(module.orCreateNbt))) {
                true -> {
                    cursor.set(ItemStack.EMPTY)
                    player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8f, 1.2f + player.world.getRandom().nextFloat() * 0.4f)
                    true
                }
                false -> false
            }
        }
        return false
    }

    private fun removeModule(stack: ItemStack, player: PlayerEntity, cursor: StackReference): Boolean {
        if (getIfEditable(stack)) {
            val modularTool = DisassembledModularTool.deserialize(stack.orCreateNbt)
            modularTool.removePart().also {
                return when (modularTool.removePart()) {
                    null -> false
                    else -> {
                        if (it != null) {
                            val cursorStack = Modulus.MODULE.defaultStack
                            cursorStack.nbt = it.serialize()
                            cursor.set(cursorStack)
                            player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8f, 1.2f + player.world.getRandom().nextFloat() * 0.4f)
                            true
                        } else {
                            false
                        }
                    }
                }
            }

        }
//        if (getIfEditable(stack) && !getModuleList(stack).isEmpty()) {
//            val list = getModuleList(stack)
//            val comp = list.removeAt(list.size - 1)
//            val module = ItemStack.fromNbt(comp as NbtCompound)
//            player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8f, 0.8f + player.world.getRandom().nextFloat() * 0.4f)
//            cursor.set(module)
//            return true
//        } else if (getIfEditable(stack) && getNbt(stack, "modulus:tool_rod") != null) {
//            val module = stack.getOrCreateNbt()["modulus:tool_rod"]
//            val item = ItemStack.fromNbt(module as NbtCompound?)
//            stack.getOrCreateNbt().remove("modulus:tool_rod")
//            player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8f, 0.8f + player.world.getRandom().nextFloat() * 0.4f)
//            cursor.set(item)
//            return true
//        }
        return false
    }

    override fun isItemBarVisible(stack: ItemStack): Boolean {
        return if (getIfEditable(stack)) {
            DisassembledModularTool.deserialize(stack.orCreateNbt).getPartLength()  * 13 / 3 > 0
        } else {
            val tool = AssembledModularTool.deserialize(stack.orCreateNbt)
            when (tool == null) {
                true -> { false }
                false -> { tool.getDamage() * 13 / tool.getDurability() > 1 }
            }

        }
    }

    override fun getItemBarStep(stack: ItemStack): Int {
        return if (getIfEditable(stack)) {
            DisassembledModularTool.deserialize(stack.orCreateNbt).getPartLength()  * 13 / 3
        } else {
            val tool = AssembledModularTool.deserialize(stack.orCreateNbt)!!
            tool.getDamage() * 13 / tool.getDurability()
        }
    }

    // From red to green as the tool gets more complete!
    override fun getItemBarColor(stack: ItemStack): Int {
        return if (getIfEditable(stack)) {
            val length = DisassembledModularTool.deserialize(stack.orCreateNbt).getPartLength()
            if (length <= 1) {
                MathHelper.color(1.0f, 0.4f, 0.4f)
            } else if (length <= 2) {
                MathHelper.color(0.7f, 0.7f, 0.4f)
            } else {
                MathHelper.color(0.4f, 1.0f, 0.4f)
            }
        } else {
            val tool = AssembledModularTool.deserialize(stack.orCreateNbt)!!
            val f = max(0.0, ((tool.getDurability().toFloat() - tool.getDamage().toFloat()) / tool.getDurability().toFloat()).toDouble()).toFloat()
            MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f)
        }
    }

    override fun isSuitableFor(state: BlockState): Boolean {
        // todo fix
        return true
    }

    companion object {
        const val MAX_STORAGE = 3
        fun getNbt(stack: ItemStack, id: String): String? {
            return stack.getOrCreateNbt().getString("modulus:$id")
        }

        fun setNbt(stack: ItemStack, id: String, value: String?) {
            stack.getOrCreateNbt().putString("modulus:$id", value)
        }


        //	@Override
        //	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        //		return true;
        //
        ////		if (stack.getOrCreateNbt().getInt("Damage") <= 0) {
        ////			stack.getOrCreateNbt().putInt("Damage", 0);
        ////		}
        ////		var effective = getEffectiveBlocks(stack);
        ////		for (TagKey<Block> tagKey : effective) {
        ////			if (state.isIn(tagKey)) {
        ////				damage(stack, 1, miner);
        ////				return true;
        ////			}
        ////		}
        ////		damage(stack, 2, miner);
        ////		return true;
        //	}
        public fun getMiningSpeed(stack: ItemStack): Float? {
            return if (!getIfEditable(stack)) {
                AssembledModularTool.deserialize(stack.orCreateNbt)!!.getMiningSpeed()
            } else {
                null
            }
        }

        fun getMiningLevel(stack: ItemStack): Int? {
            return if (!getIfEditable(stack)) {
                AssembledModularTool.deserialize(stack.orCreateNbt)!!.getMiningLevel()
            } else {
                null
            }
        }

        fun getDurability(stack: ItemStack): Int? {
            return if (!getIfEditable(stack)) {
                AssembledModularTool.deserialize(stack.orCreateNbt)!!.getDurability()
            } else {
                null
            }
        }

        fun create(tool: ToolItem?): ItemStack {
            val id = Registries.ITEM.getId(tool);


            val item = Modulus.MODULAR_TOOL.defaultStack
            val nbt = AssembledModularTool(
                    Head(Head.ModuleSide.A, 0, HeadMaterial(id)),
                    Head(Head.ModuleSide.B, 0, HeadMaterial(id)),
                    Handle()
            ).serialize()
            item.nbt = nbt;

            return item
        }

        fun toggleIfEditable(stack: ItemStack, player: LivingEntity) {

            if (getIfEditable(stack)) {
                if (DisassembledModularTool.deserialize(stack.orCreateNbt).canAssemble()) {
                    stack.getOrCreateNbt().putBoolean("modulus:is_editable", !getIfEditable(stack))
                    player.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.5f, 0.8f + player.world.getRandom().nextFloat() * 0.4f)
                } else {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.8f, 1f)
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.7f, 1f)
                }
            } else {
                stack.getOrCreateNbt().putBoolean("modulus:is_editable", !getIfEditable(stack))
                player.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.5f, 0.8f + player.world.getRandom().nextFloat() * 0.4f)

            }
        }

//        // Get size of module list
//        fun getModuleOccupancy(stack: ItemStack): Int {
//            if (getToolTod(stack)!!.isEmpty) {
//                return 0
//            }
//            return if (getModuleList(stack).isEmpty()) 1 else getModuleList(stack).size + 1
//        }
//
//        // Gets modules. Tries to stop itself from getting a null value...
//        fun getModuleList(stack: ItemStack): NbtList {
//            if (!stack.getOrCreateNbt().contains("modulus:modules")) {
//                stack.getOrCreateNbt().put("modulus:modules", NbtList())
//            }
//            return stack.getOrCreateNbt().getList("modulus:modules", NbtElement.COMPOUND_TYPE.toInt())
//        }
//
//        fun getToolTod(stack: ItemStack): NbtCompound? {
//            return stack.getOrCreateNbt().getCompound("modulus:tool_rod")
//        }

        fun getIfEditable(stack: ItemStack): Boolean {
            return stack.getOrCreateNbt().getBoolean("modulus:is_editable")
        }
    }
}
