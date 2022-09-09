package lol.j0.modulus.item;

import lol.j0.modulus.ModulusMath;
import lol.j0.modulus.ToolTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static lol.j0.modulus.Modulus.*;

public class ModularToolItem extends Item {
	public static final int MAX_STORAGE = 3;

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

		damage(stack, 1, attacker);
		return true;
	}

	private void damage(ItemStack stack, int amount, LivingEntity wielder) {
		if (!getIfEditable(stack) && !wielder.world.isClient && (!(wielder instanceof PlayerEntity) || !((PlayerEntity) wielder).getAbilities().creativeMode)) {
			stack.getOrCreateNbt().putInt("Damage", stack.getOrCreateNbt().getInt("Damage") + amount);
			if (stack.getOrCreateNbt().getInt("Damage") >= getDurability(stack)) {
				if ((wielder instanceof PlayerEntity)) {
					wielder.playSound(SoundEvents.ENTITY_ITEM_BREAK);
					toggleIfEditable(stack, wielder);
				} else {
					wielder.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					wielder.playSound(SoundEvents.ENTITY_ITEM_BREAK);
				}
			}
		}
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (stack.getOrCreateNbt().getInt("Damage") <= 0) {
			stack.getOrCreateNbt().putInt("Damage", 0);
		}
		var effective = getEffectiveBlocks(stack);
		for (TagKey<Block> tagKey : effective) {
			if (state.isIn(tagKey)) {
				damage(stack, 1, miner);
				return true;
			}
		}
		damage(stack, 2, miner);
		return true;
	}

	public static Float getMiningSpeed(ItemStack stack) {
		var module_a = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(1)));
		var module_b = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(2)));

		if (module_a == null || module_b == null) {
			stack.setCount(0);
			return 1f;
		}

		if( ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(1))) == ToolTypes.BUTT ^ ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(2)))  == ToolTypes.BUTT) {
			return ModulusMath.average(new Float[]{module_a.miningSpeed, module_b.miningSpeed}) + 1f;
		}

		return ModulusMath.average(new Float[]{module_a.miningSpeed, module_b.miningSpeed});
	}

	public static int getMiningLevel(ItemStack stack) {
		var module_a = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(1))).miningLevel;
		var module_b = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(2))).miningLevel;


		if ( ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(1))) == ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(2)))) {
			return ModulusMath.average(new int[]{module_a, module_b});
		} else {
			return ModulusMath.average(new int[]{module_a, module_b}) - 1;
		}
	}

	public static int getDurability(ItemStack stack) {

		if ( !stack.getOrCreateNbt().getBoolean("IsSlotBOccupied") ||
				!stack.getOrCreateNbt().getBoolean("IsSlotAOccupied") ||
				!stack.getOrCreateNbt().getBoolean("IsRodOccupied")) {
			return 99999;
		}


		var module_a = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(1)));
		var module_b = ModuleItem.getMaterial(ItemStack.fromNbt(getModuleList(stack).getCompound(2)));

		if (module_a == null || module_b == null) {
			stack.setCount(0);
			return 99;
		}

		if( ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(1))) == ToolTypes.BUTT ^ ModuleItem.getType(ItemStack.fromNbt(getModuleList(stack).getCompound(2)))  == ToolTypes.BUTT) {
			return (int) (ModulusMath.average(new int[]{module_a.itemDurability, module_b.itemDurability}) * 1.2);
		} else {
			return ModulusMath.average(new int[]{module_a.itemDurability, module_b.itemDurability});
		}
	}

	@Override
	public int getEnchantability() {
		LOGGER.info("sorry this is not complete yet");
		return 30;
	}

	private TagKey<Block>[] getEffectiveBlocks(ItemStack stack) {

		TagKey<Block>[] tag_list_a = new TagKey[2];
		int pointer = 0;
		var list = getModuleList(stack);

		for (NbtElement module: list) {

			var item = ItemStack.fromNbt((NbtCompound) module);
			if (item.getNbt() != null) {
			}

			if (item.getNbt() != null && item.getNbt().getString("type").equals("pickaxe")) {
				tag_list_a[pointer] = BlockTags.PICKAXE_MINEABLE;
				pointer++;
			} else if (item.getNbt() != null && item.getNbt().getString("type").equals("axe")) {
				tag_list_a[pointer] = BlockTags.AXE_MINEABLE;
				pointer++;
			}else if (item.getNbt() != null && item.getNbt().getString("type").equals("shovel")) {
				tag_list_a[pointer] = BlockTags.SHOVEL_MINEABLE;
				pointer++;
			} else if (item.getNbt() != null && item.getNbt().getString("type").equals("hoe")) {
				tag_list_a[pointer] = BlockTags.HOE_MINEABLE;
				pointer++;
			}
		}
		for (int i = 0; i < tag_list_a.length; i++) {
			TagKey<Block> x = tag_list_a[i];
			if (x == null) {
				tag_list_a[i] = BlockTags.FIRE;
			}
		}

		return tag_list_a;
	}


	public static ItemStack create(ToolItem tool) {
		var modTool = MODULAR_TOOL.getDefaultStack();
		var tool_rod = TOOL_ROD.getDefaultStack();
		var module_a = MODULE.getDefaultStack();
		var module_b = MODULE.getDefaultStack();

		modTool.getOrCreateNbt().putBoolean("IsEditable", !getIfEditable(modTool));

		var list = getModuleList(modTool);

		tool_rod.getOrCreateNbt().putString("material", "default");

		ItemStack[] itemStacks = new ItemStack[]{module_a, module_b};
		for (int i = 0; i < itemStacks.length; i++) {

			ItemStack module = itemStacks[i];
			var type = tool.asItem().toString().split("_")[1].toLowerCase();
			if (i == 1 && (type.equalsIgnoreCase("hoe") || type.equalsIgnoreCase("axe"))) {
				module.getOrCreateNbt().putString("type", "butt");
			} else {
				module.getOrCreateNbt().putString("type", type);
			}
			module.getOrCreateNbt().putString("material", tool.getMaterial().toString().toLowerCase());
			module.getOrCreateNbt().putString("tool_name", String.valueOf(tool.asItem()));
			module.getOrCreateNbt().putString("namespace", "minecraft");
		}
		module_a.getOrCreateNbt().putString("side", "a");
		module_b.getOrCreateNbt().putString("side", "b");

		modTool.getOrCreateNbt().putBoolean("IsRodOccupied", true);
		modTool.getOrCreateNbt().putBoolean("IsSlotAOccupied", true);
		modTool.getOrCreateNbt().putBoolean("IsSlotBOccupied", true);

		var comp_t = new NbtCompound();
		tool_rod.writeNbt(comp_t);
		list.add(comp_t);
		var comp_a = new NbtCompound();
		module_a.writeNbt(comp_a);
		list.add(comp_a);
		var comp_b = new NbtCompound();
		module_b.writeNbt(comp_b);
		list.add(comp_b);

		return modTool;
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {

		if (clickType == ClickType.RIGHT && stack.getNbt() != null && !stack.getNbt().getBoolean("Finished")) {
			if (otherStack.isEmpty()) {
				return removeModule(stack, player, cursorStackReference);
			} {
				return addModule(stack, otherStack, player, cursorStackReference);
			}
		}
		return false;
	}
	public static void toggleIfEditable(ItemStack stack, LivingEntity player) {
		if ( stack.getOrCreateNbt().getBoolean("IsSlotBOccupied") && stack.getOrCreateNbt().getBoolean("IsSlotAOccupied") && stack.getOrCreateNbt().getBoolean("IsRodOccupied")) {
			stack.getOrCreateNbt().putBoolean("IsEditable", !getIfEditable(stack));
			player.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.5F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
		} else {
			player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.8f, 1);
	     	player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.7f, 1);
		}
	}
	private boolean addModule(ItemStack stack, ItemStack module, PlayerEntity player, StackReference cursor) {
		NbtList list = getModuleList(stack);

		if (stack.getOrCreateNbt().getBoolean("IsRodOccupied") && module.isOf(TOOL_ROD)){
			return false;
		} else if (stack.getOrCreateNbt().getBoolean("IsSlotAOccupied")
				&& stack.getOrCreateNbt().getBoolean("IsSlotBOccupied") && module.isOf(MODULE) ) {
			return false;
		}

		if (getIfEditable(stack) && list.size() <= MAX_STORAGE-1) {
			NbtCompound comp = new NbtCompound();
			module.writeNbt(comp);
			cursor.set(ItemStack.EMPTY);
			if (module.isOf(TOOL_ROD)) {
				list.add(0, comp);
				player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
				stack.getOrCreateNbt().putBoolean("IsRodOccupied", true);
			} else if (module.isOf(MODULE)) {
				list.add(comp);
				player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 1.2F + player.getWorld().getRandom().nextFloat() * 0.4F);
				if ( stack.getOrCreateNbt().getBoolean("IsSlotAOccupied") ) {
					stack.getOrCreateNbt().putBoolean("IsSlotBOccupied", true);
				} else {
					stack.getOrCreateNbt().putBoolean("IsSlotAOccupied", true);
				}
			} else {
				return false;
			}
			return true;
		}
		return false;
	}
	private boolean removeModule(ItemStack stack, PlayerEntity player, StackReference cursor) {
		if (getIfEditable(stack) && !getModuleList(stack).isEmpty()) {
			NbtList list = getModuleList(stack);
			NbtElement comp = list.remove(list.size() - 1);
			ItemStack module = ItemStack.fromNbt((NbtCompound) comp);
			player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
			cursor.set(module);
			if ( stack.getOrCreateNbt().getBoolean("IsSlotBOccupied") ) {
				stack.getOrCreateNbt().putBoolean("IsSlotBOccupied", false);
			} else if ( stack.getOrCreateNbt().getBoolean("IsSlotAOccupied") ) {
				stack.getOrCreateNbt().putBoolean("IsSlotAOccupied", false);
			} else {
				stack.getOrCreateNbt().putBoolean("IsRodOccupied", false);
			}
			return true;
		}
		return false;
	}
	public boolean isItemBarVisible(ItemStack stack) {
		if (getIfEditable(stack)) {
			return getModuleOccupancy(stack) > 0;
		} else {
			return stack.getOrCreateNbt().getInt("Damage") * 13 / getDurability(stack) > 1;
		}

	}
	public int getItemBarStep(ItemStack stack) {
		if (getIfEditable(stack)) {
			return getModuleOccupancy(stack) * 13 / MAX_STORAGE;
		} else {
			return stack.getOrCreateNbt().getInt("Damage") * 13 / getDurability(stack);
		}
	}

	// From red to green as the tool gets more complete!
	@Override
	public int getItemBarColor(ItemStack stack) {
		if (getIfEditable(stack)) {
			if (getModuleOccupancy(stack) <= 1) {
				return MathHelper.packRgb(1.0F, 0.4F, 0.4F);
			} else if (getModuleOccupancy(stack) <= 2) {
				return MathHelper.packRgb(0.7F, 0.7F, 0.4F);
			} else {
				return MathHelper.packRgb(0.4F, 1.0F, 0.4F);
			}
		} else {
			float f = Math.max(0.0F, ((float)getDurability(stack) - (float)stack.getOrCreateNbt().getInt("Damage")) / (float)getDurability(stack));
			return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);

		}
	}

	// Get modules inside tool.
	public static int getModuleOccupancy(ItemStack stack) {
		return getModuleList(stack).isEmpty() ?  0 : getModuleList(stack).size() ;
	}

	// Gets modules. Tries to stop itself from getting a null value...
	public static NbtList getModuleList(ItemStack stack) {
		if (!stack.getOrCreateNbt().contains("Modules")) {
			stack.getOrCreateNbt().put("Modules", new NbtList());
		}
		return stack.getOrCreateNbt().getList("Modules", NbtElement.COMPOUND_TYPE);
	}
	public static boolean getIfEditable(ItemStack stack) {
		return stack.getOrCreateNbt().getBoolean("IsEditable");
	}

	public ModularToolItem(Settings settings) {
		super(settings);
	}
}


/*
add			entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
remove		entity.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
finalize	player.playSound(SoundEvents.BLOCK_ANVIL_HIT, 0.5F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
fnlz error 	client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.8f, 1));
	     &	client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.7f, 1));

 */
