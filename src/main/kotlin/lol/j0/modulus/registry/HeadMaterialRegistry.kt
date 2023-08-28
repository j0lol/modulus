package lol.j0.modulus.registry

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lol.j0.modulus.api.HeadMaterial
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.NoSuchElementException

object HeadMaterialRegistry: RegistryInterface<HeadMaterial> {
    override var list: Object2ObjectOpenHashMap<Identifier, HeadMaterial> = Object2ObjectOpenHashMap()
}
