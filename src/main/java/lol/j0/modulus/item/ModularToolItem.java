package lol.j0.modulus.item;

import lol.j0.modulus.Modulus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static lol.j0.modulus.Modulus.*;

public class ModularToolItem extends Item {
	public static final int MAX_STORAGE = 3;
	private TagKey<Block> effectiveBlocks;

	@Override
	public int getEnchantability() {
		LOGGER.info("hello minecraft");
		return 30;
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		int i = this.getMiningLevel();
		if (i < 3 && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
			return false;
		} else if (i < 2 && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
			return false;
		} else {
			return (i >= 1 || !state.isIn(BlockTags.NEEDS_STONE_TOOL)) &&
				state.isIn(this.getEffectiveBlocks()[0]) || state.isIn(this.getEffectiveBlocks()[1]);
		}
	}

	public int getMiningLevel() {
		return 3;
	}
	private TagKey<Block>[] getEffectiveBlocks() {

		TagKey<Block>[] tag_list_a = new TagKey[2];
		int pointer = 0;
		var list = getModuleList(this.getDefaultStack());

		for (NbtElement module: list) {

			var item = ItemStack.fromNbt((NbtCompound) module);
			if (item.getNbt() != null) {
				LOGGER.info("type of module " + item.getNbt().getString("type"));
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

		for (ItemStack module: new ItemStack[]{module_a, module_b} ) {
			module.getOrCreateNbt().putString("type", tool.asItem().toString()
					.split("_")[1].toLowerCase());
			module.getOrCreateNbt().putString("material", tool.getMaterial().toString().toLowerCase());
			module.getOrCreateNbt().putString("tool_name", tool.asItem().getName().toString());
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
	public static void toggleIfEditable(ItemStack stack, PlayerEntity player) {
		stack.getOrCreateNbt().putBoolean("IsEditable", !getIfEditable(stack));
		player.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.5F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
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
		return getIfEditable(stack) && getModuleOccupancy(stack) > 0;
	}
	public int getItemBarStep(ItemStack stack) {
		return getModuleOccupancy(stack) * 13 / MAX_STORAGE;
	}

	// From red to green as the tool gets more complete!
	@Override
	public int getItemBarColor(ItemStack stack) {
		if (getModuleOccupancy(stack) <= 1) {
			return MathHelper.packRgb(1.0F, 0.4F, 0.4F);
		} else if (getModuleOccupancy(stack) <= 2) {
			return MathHelper.packRgb(0.7F, 0.7F, 0.4F);
		} else {
			return MathHelper.packRgb(0.4F, 1.0F, 0.4F);
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



//	@Override
//	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//		// todo: Check if it's a tool
//		// If so, use it as a tool.
//		// Else, do nothing.
//		ItemStack itemStack = user.getStackInHand(hand);
//		return TypedActionResult.fail(itemStack);
//	}

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
