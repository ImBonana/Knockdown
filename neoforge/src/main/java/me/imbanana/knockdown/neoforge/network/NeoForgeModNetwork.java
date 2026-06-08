package me.imbanana.knockdown.neoforge.network;

import me.imbanana.knockdown.network.ModNetwork;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoForgeModNetwork {
    private static PayloadRegistrar registrar;

    public static void registerC2S(IEventBus bus) {
        bus.addListener(NeoForgeModNetwork::registerC2SPayloads);
    }

    private static void registerC2SPayloads(RegisterPayloadHandlersEvent event) {
        registrar = event.registrar("1");
        ModNetwork.registerC2S(NeoForgeModNetwork::registerSingleC2S);
        ModNetwork.registerS2C(NeoForgeModNetwork::registerSingleS2C);
    }

    private static <T extends CustomPacketPayload> void registerSingleC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
            ModNetwork.ServerHandler<T> handler
    ) {
        registrar.playToServer(type, streamCodec, (customPacketPayload, iPayloadContext) -> {
            handler.handle(customPacketPayload, new ModNetwork.ServerContext() {
                @Override
                public ServerPlayer getPlayer() {
                    return (ServerPlayer) iPayloadContext.player();
                }
            });
        });
    }

    private static <T extends CustomPacketPayload> void registerSingleS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
            ModNetwork.ClientHandler<T> handler
    ) {
        registrar.playToClient(type, streamCodec, (customPacketPayload, iPayloadContext) -> {
            handler.handle(customPacketPayload, new ModNetwork.ClientContext() {
                @Override
                public LocalPlayer getPlayer() {
                    return (LocalPlayer) iPayloadContext.player();
                }
            });
        });
    }

    public static void registerClient() {
        ModNetwork.registerClient(ClientPacketDistributor::sendToServer);
    }

    public static void registerServer() {
        ModNetwork.registerServer(PacketDistributor::sendToPlayer);
    }
}
