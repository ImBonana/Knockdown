package me.imbanana.knockdown.neoforge.data;

import com.google.common.base.Predicates;
import com.mojang.serialization.MapCodec;
import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.data.ModDataAttachments;
import me.imbanana.knockdown.util.KnockdownData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class NeoForgeModDataAttachment {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, KnockdownMod.MOD_ID);

    private static final Supplier<AttachmentType<KnockdownData>> KNOCKDOWN_DATA = ATTACHMENT_TYPES.register(
            "knockdown",
            () -> AttachmentType.builder(() -> KnockdownData.DEFAULT)
                    .sync((iAttachmentHolder, serverPlayer) -> true, KnockdownData.STREAM_CODEC)
                    .serialize(KnockdownData.CODEC.fieldOf("knockdown"))
                    .build()
    );

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);

        ModDataAttachments.KNOCKDOWN = new ModDataAttachments.DataAttachment<>(
                entity -> entity.getData(KNOCKDOWN_DATA),
                (entity, data) -> entity.setData(KNOCKDOWN_DATA, data),
                entity -> entity.hasData(KNOCKDOWN_DATA)
        );
    }
}
