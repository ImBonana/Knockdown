package me.imbanana.knockdown.neoforge;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.neoforge.data.NeoForgeModDataAttachment;
import me.imbanana.knockdown.neoforge.network.NeoForgeModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(KnockdownMod.MOD_ID)
public final class KnockdownModNeoForge {
    public KnockdownModNeoForge(IEventBus modBus) {
        // Run our common setup.
        KnockdownMod.init();
        NeoForgeModDataAttachment.register(modBus);
        NeoForgeModNetwork.registerC2S(modBus);
        NeoForgeModNetwork.registerServer();

        modBus.addListener(this::playerJoinEvent);
    }

    private void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ((IKnockdownable) event.getEntity()).syncTicksLeft();
    }
}
