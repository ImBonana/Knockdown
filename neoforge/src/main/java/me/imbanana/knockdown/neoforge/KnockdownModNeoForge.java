package me.imbanana.knockdown.neoforge;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.neoforge.data.NeoForgeModDataAttachment;
import me.imbanana.knockdown.neoforge.network.NeoForgeModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@Mod(KnockdownMod.MOD_ID)
public final class KnockdownModNeoForge {
    public KnockdownModNeoForge(IEventBus modBus) {
        // Run our common setup.
        KnockdownMod.init();
        NeoForgeModDataAttachment.register(modBus);
        NeoForgeModNetwork.registerC2S(modBus);
        NeoForgeModNetwork.registerServer();

        NeoForge.EVENT_BUS.addListener(this::playerJoinEvent);

        NeoForge.EVENT_BUS.addListener(this::onBlockClick);
        NeoForge.EVENT_BUS.addListener(this::onEntityRightClick);
        NeoForge.EVENT_BUS.addListener(this::harvestSpeed);
        NeoForge.EVENT_BUS.addListener(this::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(this::onBreakBlock);
    }

    private void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ((IKnockdownable) event.getEntity()).syncTicksLeft();
    }


    // cancel interaction
    private void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        boolean isKnockedDown = ((IKnockdownable) event.getEntity()).isKnockedDown();

        event.setCancellationResult(InteractionResult.FAIL);
        if (isKnockedDown) event.setUseItem(TriState.FALSE);
        if (isKnockedDown) event.setUseBlock(TriState.FALSE);
        event.setCanceled(isKnockedDown);
    }

    private void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        boolean isKnockedDown = ((IKnockdownable) event.getEntity()).isKnockedDown();
        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(isKnockedDown);
    }

    private void harvestSpeed(PlayerEvent.BreakSpeed event) {
        boolean isKnockedDown = ((IKnockdownable) event.getEntity()).isKnockedDown();
        if (isKnockedDown) event.setNewSpeed(0);
    }

    private void onAttackEntity(AttackEntityEvent event) {
        boolean isKnockedDown = ((IKnockdownable) event.getEntity()).isKnockedDown();
        event.setCanceled(isKnockedDown);
    }

    private void onBreakBlock(BreakBlockEvent event) {
        boolean isKnockedDown = ((IKnockdownable) event.getPlayer()).isKnockedDown();
        event.setCanceled(isKnockedDown);
    }
}
