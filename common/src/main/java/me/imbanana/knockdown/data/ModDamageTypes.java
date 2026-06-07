package me.imbanana.knockdown.data;

import me.imbanana.knockdown.KnockdownMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> BLEED_OUT = ResourceKey.create(Registries.DAMAGE_TYPE, KnockdownMod.idOf("bleed_out"));
}
