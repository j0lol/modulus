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
package lol.j0.modulus.resource

import com.google.gson.JsonObject
import lol.j0.modulus.client.ModulusClient
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

class ModelBuilder(parent: Identifier) {
    private val json = JsonObject()
    private var textures: JsonObject? = null

    init {
        json.addProperty("parent", parent.toString())
    }

    fun texture(name: String?, id: Identifier): ModelBuilder {
        if (textures == null) {
            json.add("textures", JsonObject().also { textures = it })
        }
        textures!!.addProperty(name, id.toString())
        return this
    }

    fun toJson(): JsonObject {
        return json
    }

    fun register(id: Identifier): Identifier {
        ModulusClient.RESOURCE_PACK.putJson(ResourceType.CLIENT_RESOURCES,
                Identifier(id.namespace, "models/" + id.path),
                toJson())
        return id
    }
}
