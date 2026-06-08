package me.imbanana.knockdown.network;

import me.imbanana.knockdown.network.c2s.KnockdownActionPayloadC2S;
import me.imbanana.knockdown.network.s2c.KnockdownSyncPayloadS2C;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class ModNetwork {
    private static SendToServerFunction sendToServerFunction;
    private static SendToPlayerFunction sendToPlayerFunction;

    public static void registerC2S(C2SPayloadRegistry registrar) {
        registrar.register(KnockdownActionPayloadC2S.TYPE, KnockdownActionPayloadC2S.STREAM_CODEC, KnockdownActionPayloadC2S::receiveServer);
    }

    public static void registerS2C(S2CPayloadRegistry registrar) {
        registrar.register(KnockdownSyncPayloadS2C.TYPE, KnockdownSyncPayloadS2C.STREAM_CODEC, KnockdownSyncPayloadS2C::receiveClient);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        sendToServerFunction.send(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        sendToPlayerFunction.send(player, payload);
    }

    public static void registerClient(SendToServerFunction sendToServerFunction) {
        ModNetwork.sendToServerFunction = sendToServerFunction;
    }

    public static void registerServer(SendToPlayerFunction sendToPlayerFunction) {
        ModNetwork.sendToPlayerFunction = sendToPlayerFunction;
    }

    @FunctionalInterface
    public interface C2SPayloadRegistry {
        <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ServerHandler<T> handler);
    }

    @FunctionalInterface
    public interface S2CPayloadRegistry {
        <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ClientHandler<T> handler);
    }

    @FunctionalInterface
    public interface ServerHandler<T> {
        void handle(T payload, ServerContext context);
    }

    public interface ServerContext {
        ServerPlayer getPlayer();
    }

    @FunctionalInterface
    public interface ClientHandler<T> {
        void handle(T payload, ClientContext context);
    }

    public interface ClientContext {
        LocalPlayer getPlayer();
    }

    @FunctionalInterface
    public interface SendToServerFunction {
        void send(CustomPacketPayload payload);
    }

    @FunctionalInterface
    public interface SendToPlayerFunction {
        void send(ServerPlayer player, CustomPacketPayload payload);
    }
}
