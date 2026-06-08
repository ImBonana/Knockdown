package me.imbanana.knockdown.fabric.network;

import me.imbanana.knockdown.network.ModNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricModNetwork {
    public static void registerC2S() {
        ModNetwork.registerC2S(FabricModNetwork::registerSingleC2S);
        ModNetwork.registerS2C(FabricModNetwork::registerSingleS2C);
    }

    private static <T extends CustomPacketPayload> void registerSingleC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
            ModNetwork.ServerHandler<T> handler
    ) {
        PayloadTypeRegistry.serverboundPlay().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
            handler.handle(payload, new ModNetwork.ServerContext() {
                @Override
                public ServerPlayer getPlayer() {
                    return context.player();
                }
            })
        );
    }

    private static <T extends CustomPacketPayload> void registerSingleS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
            ModNetwork.ClientHandler<T> handler
    ) {
        PayloadTypeRegistry.clientboundPlay().register(type, streamCodec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                handler.handle(payload, new ModNetwork.ClientContext() {
                    @Override
                    public LocalPlayer getPlayer() {
                        return context.player();
                    }
                })
        );
    }

    public static void registerClient() {
        ModNetwork.registerClient(ClientPlayNetworking::send);
    }

    public static void registerServer() {
        ModNetwork.registerServer(ServerPlayNetworking::send);
    }
}
