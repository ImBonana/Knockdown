package me.imbanana.knockdown.neoforge;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.neoforge.data.NeoForgeModDataAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(KnockdownMod.MOD_ID)
public final class KnockdownModNeoForge {
    public KnockdownModNeoForge(IEventBus modBus) {
        // Run our common setup.
        KnockdownMod.init();
        NeoForgeModDataAttachment.register(modBus);
    }
}
