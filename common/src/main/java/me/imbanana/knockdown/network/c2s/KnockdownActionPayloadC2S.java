package me.imbanana.knockdown.network.c2s;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record KnockdownActionPayloadC2S(Action action) implements CustomPacketPayload {
    public static final Identifier ID = KnockdownMod.idOf("knockdown_action");
    public static final CustomPacketPayload.Type<KnockdownActionPayloadC2S> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, KnockdownActionPayloadC2S> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, KnockdownActionPayloadC2S>() {
        @Override
        public KnockdownActionPayloadC2S decode(RegistryFriendlyByteBuf input) {
            return new KnockdownActionPayloadC2S(input.readEnum(Action.class));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf output, KnockdownActionPayloadC2S value) {
            output.writeEnum(value.action);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void receiveServer(KnockdownActionPayloadC2S payload, ModNetwork.ServerContext context) {
        IKnockdownable player = (IKnockdownable) context.getPlayer();

        if (player.isKnockedDown()) {
            player.setFastBleedOut(
                    switch (payload.action()) {
                        case Action.START_FASTER_BLEED_OUT -> true;
                        case Action.END_FASTER_BLEED_OUT -> false;
                    }
            );
        } else {
            player.setFastBleedOut(false);
        }
    }

    public enum Action {
        START_FASTER_BLEED_OUT,
        END_FASTER_BLEED_OUT
    }
}
