package me.imbanana.knockdown.fabric;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.data.ModDataAttachments;
import me.imbanana.knockdown.fabric.data.FabricModDataAttachment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.Entity;

public final class KnockdownModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KnockdownMod.init();
        FabricModDataAttachment.register();
    }
}
