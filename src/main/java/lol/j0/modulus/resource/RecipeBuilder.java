/*
 * Copyright (c) 2021-2022 LambdAurora <email@lambdaurora.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lol.j0.modulus.resource;

import com.google.gson.JsonObject;
import lol.j0.modulus.client.ModulusClient;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeBuilder {
	private final JsonObject json = new JsonObject();
	private JsonObject ingredient;

	public RecipeBuilder(Identifier type) {
		this.json.addProperty("type", type.toString());
	}

	public RecipeBuilder ingredient(Identifier id) {
		if (this.ingredient == null) {
			this.json.add("ingredient", this.ingredient = new JsonObject());
		}

		this.ingredient.addProperty("tag", id.toString());

		return this;
	}

	public RecipeBuilder result(Identifier id) {
		this.json.addProperty("result", id.toString());
		return this;
	}

	public RecipeBuilder count(Identifier id) {
		this.json.addProperty("count", id.toString());
		return this;
	}

	public JsonObject toJson() {
		return this.json;
	}

	public Identifier register(Identifier id) {
		ModulusClient.RESOURCE_PACK.putJson(ResourceType.SERVER_DATA,
				new Identifier(id.getNamespace(), "recipes/" + id.getPath()),
				this.toJson());
		return id;
	}
}
