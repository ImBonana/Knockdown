package me.imbanana.knockdown.neoforge;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.neoforge.data.NeoForgeModDataAttachment;
import me.imbanana.knockdown.neoforge.network.NeoForgeModNetwork;
import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(KnockdownMod.MOD_ID)
public final class KnockdownModNeoForge {
    public KnockdownModNeoForge(IEventBus modBus) {
        // Run our common setup.
        KnockdownMod.init();
        NeoForgeModDataAttachment.register(modBus);
        NeoForgeModNetwork.registerC2S(modBus);
        NeoForgeModNetwork.registerServer();
    }

    private void PlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ((IKnockdownable) event.getEntity()).syncTicksLeft();
    }
}
