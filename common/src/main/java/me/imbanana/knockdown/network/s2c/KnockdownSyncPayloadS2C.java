package me.imbanana.knockdown.network.s2c;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record KnockdownSyncPayloadS2C(int knockdownTicksLeft) implements CustomPacketPayload {
    public static final Identifier ID = KnockdownMod.idOf("knockdown_sync");
    public static final CustomPacketPayload.Type<KnockdownSyncPayloadS2C> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, KnockdownSyncPayloadS2C> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, KnockdownSyncPayloadS2C::knockdownTicksLeft, KnockdownSyncPayloadS2C::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void receiveClient(KnockdownSyncPayloadS2C payload, ModNetwork.ClientContext context) {
        ((IKnockdownable) context.getPlayer()).setTicksLeft(payload.knockdownTicksLeft);
    }
}
