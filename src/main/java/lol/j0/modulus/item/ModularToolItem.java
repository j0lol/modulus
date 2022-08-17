package lol.j0.modulus.item;

import lol.j0.modulus.Modulus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static lol.j0.modulus.Modulus.TOOL_HAMMER;
import static lol.j0.modulus.Modulus.TOOL_ROD;

public class ModularToolItem extends Item {
	public static final int MAX_STORAGE = 3;



	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {

		// Behaviors when clicked:
		// If hammer, toggle edit mode.
		// If edit mode && tool module, add tool module if valid.
		// If edit mode && empty hand, remove tool module.
		// Else do nothing.

		if (clickType == ClickType.RIGHT && otherStack.isOf(TOOL_HAMMER)) {

			// todo: The tool checker would be here.

//			if (getModuleList(stack).size() != 0) {
				toggleIfEditable(stack, player);
				return true;
//			} else {
//				return false;
//			}
		}
		if (clickType == ClickType.RIGHT && stack.getNbt() != null && !stack.getNbt().getBoolean("Finished")) {
			if (otherStack.isEmpty()) {
				return removeModule(stack, player, cursorStackReference);
			} {
				return addModule(stack, otherStack, player, cursorStackReference);
			}
		}
		return false;
	}
	private void toggleIfEditable(ItemStack stack, PlayerEntity player) {
		stack.getOrCreateNbt().putBoolean("IsEditable", !getIfEditable(stack));
		player.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.5F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
	}
	private boolean addModule(ItemStack stack, ItemStack module, PlayerEntity player, StackReference cursor) {
		NbtList list = getModuleList(stack);

		for (NbtElement i : list) {
			if (module.getItem() == ItemStack.fromNbt((NbtCompound) i).getItem()){
				return false;
			}
		}

		if (getIfEditable(stack) && list.size() <= MAX_STORAGE-1) {
			NbtCompound comp = new NbtCompound();
			module.writeNbt(comp);
			cursor.set(ItemStack.EMPTY);
			if (module.isOf(TOOL_ROD)) {
				list.add(0, comp);
				player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
			} else if (module.isIn(TagKey.of(Registry.ITEM_KEY, Modulus.id("modular_tool_part")))) {
				list.add(comp);
				player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 0.8F, 1.2F + player.getWorld().getRandom().nextFloat() * 0.4F);
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



	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		// todo: Check if it's a tool
		// If so, use it as a tool.
		// Else, do nothing.
		ItemStack itemStack = user.getStackInHand(hand);
		return TypedActionResult.fail(itemStack);
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
