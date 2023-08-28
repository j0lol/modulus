package lol.j0.modulus.api

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.onFailure
import lol.j0.modulus.Modulus
import lol.j0.modulus.registry.HandleMaterialRegistry
import lol.j0.modulus.registry.ModulusRegistries
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

class Handle(
    override var damage: Int,
    override val material: HandleMaterial,
) : Part, DamageablePart {
    private val partId = "modulus:handle"
    override val zIndex = 0;


    var id: Identifier? = null
    override val durability: Int
        get() = material.durability

    override val repairIngredient: Ingredient
        get() = material.repairIngredient
    override fun serialize(): NbtCompound {
        val nbtCompound = NbtCompound()

        nbtCompound.putString("modulus:part", partId)

        if ( id==null ) id = HandleMaterialRegistry.list.entries.stream().filter { x -> x.value == material}.findFirst().get().key
        nbtCompound.putString("modulus:material", id.toString())
        nbtCompound.putInt("Damage", damage)
        return nbtCompound
    }

    override fun getModelID(): ModelIdentifier {
        if ( id==null ) id = HandleMaterialRegistry.list.entries.stream().filter { x -> x.value == material}.findFirst().get().key
        return ModelIdentifier(Modulus.id("default_handle" + if (broken) "_broken" else ""), "inventory")
    }

    companion object {
        fun deserialize(nbtCompound: NbtCompound): Result<Handle, ModulusDeserializeException> {
            val materialId = Identifier(nbtCompound.getString("modulus:material"))
            return Success(Handle(
                damage = nbtCompound.getInt("Damage"),
                material = ModulusRegistries.HANDLE_MATERIALS.get(materialId).onFailure { return Failure(ModulusDeserializeException()) },
            ))
        }
    }
}
