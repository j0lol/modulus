package lol.j0.modulus.item

import dev.forkhandles.result4k.*
import lol.j0.modulus.Modulus
import lol.j0.modulus.api.*
import lol.j0.modulus.item.ModularTool
import lol.j0.modulus.registry.HeadMaterialRegistry
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.StackReference
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.Slot
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.ClickType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.max

class ModularToolItem(settings: Settings?) : Item(settings) {
    private fun damage(stack: ItemStack, amount: Int, wielder: LivingEntity) {
        if (!getIfEditable(stack) && !wielder.world.isClient && (wielder !is PlayerEntity || !wielder.abilities.creativeMode)) {
            val tool = AssembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }

            tool.damage(amount)

            stack.nbt = tool.serialize()
        }
    }

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity
    ): Boolean {
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            damage(stack, 1, miner)
        }

        return true

    }
    /**
        When a tool is used on a block. Used for axe stripping and hoe tilling.
     */
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val tool = AssembledModularTool.deserialize(context.stack.orCreateNbt).onFailure { return ActionResult.PASS }
        if (tool.broken) return ActionResult.PASS
        return tool.parts.filterIsInstance<Head>().random().useOnBlock(context)

    }

    /**
     * Used to check if a tool can be enchanted
     */
    override fun isDamageable(): Boolean {
        return true
    }
    override fun isEnchantable(stack: ItemStack?): Boolean {
        return true
    }
    override fun getEnchantability(): Int {
        Modulus.LOGGER.info("sorry this is not complete yet")
        return 30
    }

    override fun onClicked(stack: ItemStack, otherStack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity, cursorStackReference: StackReference): Boolean {
        if (clickType == ClickType.RIGHT && stack.nbt != null && stack.nbt!!.getBoolean("modulus:is_editable")) {
            if (otherStack.isEmpty) {
                return removeModule(stack, slot, player, cursorStackReference)
            }
            run { return addModule(stack, otherStack, player, cursorStackReference) }
        }
        return false
    }

    private fun addModule(stack: ItemStack, module: ItemStack, player: PlayerEntity, cursor: StackReference): Boolean {

        if (getIfEditable(stack)) {
            val modularTool = DisassembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }
            return when (modularTool.addPart(Part.deserialize(module.orCreateNbt).onFailure { return false })) {
                true -> {
                    stack.nbt = modularTool.serialize()
                    cursor.set(ItemStack.EMPTY)
                    player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8f, 1.2f + player.world.getRandom().nextFloat() * 0.4f)
                    true
                }
                false -> false
            }
        }
        return false
    }

    private fun removeModule(stack: ItemStack, slot: Slot, player: PlayerEntity, cursor: StackReference): Boolean {
        if (getIfEditable(stack)) {
            val modularTool = DisassembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }
            Modulus.LOGGER.info(stack.nbt.toString())
            modularTool.removePart()?.also {

                val partStack = Modulus.MODULE.defaultStack
                partStack.nbt = it.serialize()
                cursor.set(partStack)
                stack.nbt = modularTool.serialize()

                player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8f, 1.2f + player.world.getRandom().nextFloat() * 0.4f)

                return true
            }
        }
        return false
    }

    override fun isItemBarVisible(stack: ItemStack): Boolean {
        return when (val tool = get(stack).onFailure { return false }) {
            is ModularTool.Assembled -> (tool.value.damage * 13) / tool.value.durability  > 1
            is ModularTool.Disassembled -> tool.value.getPartLength()  * 13 / 3 > 0
        }
    }

    override fun getItemBarStep(stack: ItemStack): Int {
        return when (val tool = get(stack).onFailure { return 0 }) {
            is ModularTool.Assembled -> {
                Math.round(13.0f - tool.value.damage * 13.0f / tool.value.durability)
            }
            is ModularTool.Disassembled -> {
                tool.value.getPartLength()  * 13 / 3
            }
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return when (val tool = get(stack).onFailure { return MathHelper.color(1f, 0f, 1f) }) {
            is ModularTool.Disassembled -> {
                val length = tool.value.getPartLength()
                if (length <= 1) {
                    MathHelper.color(1.0f, 0.4f, 0.4f)
                } else if (length <= 2) {
                    MathHelper.color(0.7f, 0.7f, 0.4f)
                } else {
                    MathHelper.color(0.4f, 1.0f, 0.4f)
                }
            }
            is ModularTool.Assembled -> {
                val hue = max(0.0, ((tool.value.durability.toFloat() - tool.value.damage.toFloat()) / tool.value.durability.toFloat()).toDouble()).toFloat()
                MathHelper.hsvToRgb(hue / 3.0f, 1.0f, 1.0f)
            }
        }
    }

    companion object {
        fun getMiningSpeed(stack: ItemStack, state: BlockState): Float? {
            return when (val tool = get(stack).onFailure { return null }) {
                is ModularTool.Assembled -> {
                    if (!tool.value.broken) {
                        tool.value.getMiningSpeedMultiplier(stack, state)
                    } else {
                        0F
                    }
                }
                is ModularTool.Disassembled -> null
            }
        }

        fun isSuitable(stack: ItemStack, state: BlockState): Boolean {
            return when (val tool = get(stack).onFailure { return false }) {
                is ModularTool.Assembled -> tool.value.isSuitable(state)
                is ModularTool.Disassembled -> true
            }
        }

        fun getMiningLevel(stack: ItemStack): Int? {
            return when (val tool = get(stack).onFailure { return null }) {
                is ModularTool.Assembled -> tool.value.miningLevel
                is ModularTool.Disassembled -> null
            }
        }

        fun getDurability(stack: ItemStack): Int? {
            return when (val tool = get(stack).onFailure { return null }) {
                is ModularTool.Assembled -> tool.value.durability
                is ModularTool.Disassembled -> null
            }
        }

        fun create(tool: ItemStack): Result<ItemStack, NoSuchElementException> {
            val id = Registries.ITEM.getId(tool.item);

            val handleMaterial = if (tool.item.isFireproof) {
                Modulus.NETHERITE_HANDLE
            } else {
                Modulus.DEFAULT_HANDLE
            }

            val damage = tool.orCreateNbt.getInt("Damage")

            val enchantments: List<ToolEnchantment>? =
                tool.nbt?.getList("Enchantments", NbtElement.COMPOUND_TYPE.toInt())?.filterIsInstance<NbtCompound>()
                    ?.map {
                        ToolEnchantment.deserialize(it)
                    }

            val item = Modulus.MODULAR_TOOL.defaultStack
            val nbt = AssembledModularTool(
                Head(
                    Head.ModuleSide.A,
                    0,
                    HeadMaterialRegistry.get(id).onFailure { return it },
                    id),
                Head(
                    Head.ModuleSide.B,
                    0,
                    HeadMaterialRegistry.get(id).onFailure { return it },
                    id),
                Handle(
                    damage = 0,
                    material = handleMaterial
                ),
                enchantments,
                0,
                damage
            )
                .serialize()
            item.nbt = nbt;

            return Success(item)
        }

        fun toggleIfEditable(stack: ItemStack, player: LivingEntity) {

            if (getIfEditable(stack)) {
                if (DisassembledModularTool.deserialize(stack.orCreateNbt).onFailure { throw it.get() }.canAssemble()) {
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

        fun getIfEditable(stack: ItemStack): Boolean {
            return stack.getOrCreateNbt().getBoolean("modulus:is_editable")
        }

        /**
         * Converts an ItemStack containing a tool into either an Assembled tool or a Disassembled tool, and returns in an Either
         */
        private fun get(stack: ItemStack): Result4k<ModularTool<AssembledModularTool, DisassembledModularTool>, ModulusDeserializeException> {
            return when (!getIfEditable(stack)) {
                true -> Success(ModularTool.Assembled(AssembledModularTool.deserialize(stack.orCreateNbt).onFailure { return Failure(it.get()) }))
                false -> Success(ModularTool.Disassembled(DisassembledModularTool.deserialize(stack.orCreateNbt).onFailure { return Failure(it.get()) }))
            }
        }
    }
}


private sealed class ModularTool<A, B> {
    class Assembled<A, B>(val value: A) : ModularTool<A, B>()
    class Disassembled<A, B>(val value: B) : ModularTool<A, B>()
}