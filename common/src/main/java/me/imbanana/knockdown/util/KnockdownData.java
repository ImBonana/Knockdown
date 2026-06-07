package me.imbanana.knockdown.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.imbanana.knockdown.KnockdownMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record KnockdownData(boolean isKnockedDown, Component deathMessage) {
    public static final KnockdownData DEFAULT = new KnockdownData(false, CommonComponents.EMPTY);

    public static final Codec<KnockdownData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isKnockedDown").forGetter(KnockdownData::isKnockedDown),
            ComponentSerialization.CODEC.fieldOf("deathMessage").forGetter(KnockdownData::deathMessage)
    ).apply(instance, KnockdownData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnockdownData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public KnockdownData decode(RegistryFriendlyByteBuf input) {
            return new KnockdownData(input.readBoolean(), ComponentSerialization.STREAM_CODEC.decode(input));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf output, KnockdownData value) {
            output.writeBoolean(value.isKnockedDown);
            ComponentSerialization.STREAM_CODEC.encode(output, value.deathMessage);
        }
    };

    public KnockdownData setKnockedDown(boolean knockedDown) {
        return new KnockdownData(knockedDown, deathMessage);
    }

    public KnockdownData setDeathMessage(Component deathMessage) {
        return new KnockdownData(isKnockedDown, deathMessage);
    }
}
