package lol.j0.modulus.registry

import lol.j0.modulus.api.HandleMaterial
import lol.j0.modulus.api.HeadMaterial
import net.minecraft.util.Identifier

class ModulusRegistries {
    companion object {

        val HANDLE_MATERIALS = HandleMaterialRegistry
        val HEAD_MATERIALS = HeadMaterialRegistry

        fun <I, R: RegistryInterface<I>> register(registry: R, identifier: Identifier, item: I) {
            when (registry) {
                HandleMaterialRegistry -> HandleMaterialRegistry.register(identifier, item as HandleMaterial)
                HeadMaterialRegistry -> HeadMaterialRegistry.register(identifier, item as HeadMaterial)
            }
        }
    }

}