package lol.j0.modulus.registry

import dev.forkhandles.result4k.Failure
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lol.j0.modulus.api.HeadMaterial
import net.minecraft.util.Identifier
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import lol.j0.modulus.Modulus

interface RegistryInterface<I> {

    var list: Object2ObjectOpenHashMap<Identifier, I>

    fun register(identifier: Identifier, item: I) {
        list[identifier] = item
    }

    fun get(identifier: Identifier): Result<I, NoSuchElementException> {
        return when (val item = list[identifier]) {
            null -> {
//                Modulus.LOGGER.error("Registry lookup failed on $identifier")
//                Modulus.LOGGER.error(NoSuchElementException().stackTraceToString())
                Failure(NoSuchElementException())
            }
            else -> Success(item)
        }
    }
}