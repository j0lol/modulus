package lol.j0.modulus.item;

import lol.j0.modulus.Modulus;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;


public class ToolRodItem extends Item {
	static TagKey<Item> planks = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft", "planks"));

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {

		Hashtable<String, Item> PlankNames = new Hashtable<>();

		var tool_rod_list = guessPlankItemNames();

		for (String i: tool_rod_list) {
			switch (i) {
				case "acacia" -> PlankNames.put(i, Items.ACACIA_PLANKS);
				case "birch" -> PlankNames.put(i, Items.BIRCH_PLANKS);
				case "crimson" -> PlankNames.put(i, Items.CRIMSON_PLANKS);
				case "dark_oak" -> PlankNames.put(i, Items.DARK_OAK_PLANKS);
				case "jungle" -> PlankNames.put(i, Items.JUNGLE_PLANKS);
				case "mangrove" -> PlankNames.put(i, Items.MANGROVE_PLANKS);
				case "oak" -> PlankNames.put(i, Items.OAK_PLANKS);
				case "spruce" -> PlankNames.put(i, Items.SPRUCE_PLANKS);
				case "warped" -> PlankNames.put(i, Items.WARPED_PLANKS);
				default -> PlankNames.put(i, Items.AIR);
			}
		}

//		var stick_material = stack.getOrCreateNbt().getString("material");
//		if (Objects.equals(stick_material, "vanilla") || stick_material.equals("default")) {
//			player.dropItem(Items.STICK);
//		} else if (PlankNames.containsKey(stick_material)) {
//			player.dropItem(PlankNames.get(stick_material));
//		}
		if (otherStack.isIn(planks)) {
			stack.getOrCreateNbt().putString("material", otherStack.getItem().toString().split("_planks")[0]);
			otherStack.decrement(1);
		} else if (otherStack.isOf(Items.STICK)) {
			stack.getOrCreateNbt().putString("material", "vanilla");
			otherStack.decrement(1);
		} else {
			return false;
		}

		return true;
	}

	public ToolRodItem(Settings settings) {
		super(settings);
	}
	public ArrayList<String> guessPlankItemNames() {
		String[] resources = new String[]{"acacia", "birch", "crimson", "dark_oak", "jungle", "mangrove", "oak", "spruce", "warped"};
		var out = new ArrayList<String>();

		for (String r: resources) {
			out.add(r + "_planks");
		}
		return out;
	}
}
