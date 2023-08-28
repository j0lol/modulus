package lol.j0.modulus.registry

import dev.forkhandles.result4k.Result
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lol.j0.modulus.api.HandleMaterial
import lol.j0.modulus.api.HeadMaterial
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.Objects

object HandleMaterialRegistry: RegistryInterface<HandleMaterial> {
    override var list: Object2ObjectOpenHashMap<Identifier, HandleMaterial> = Object2ObjectOpenHashMap()

}
