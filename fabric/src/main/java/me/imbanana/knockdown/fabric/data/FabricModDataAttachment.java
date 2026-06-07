package me.imbanana.knockdown.fabric.data;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.data.ModDataAttachments;
import me.imbanana.knockdown.util.KnockdownData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.chat.CommonComponents;

public class FabricModDataAttachment {
    private static final AttachmentType<KnockdownData> KNOCKDOWN_DATA = AttachmentRegistry.create(
            KnockdownMod.idOf("knockdown"),
            builder ->
                    builder.initializer(() -> KnockdownData.DEFAULT)
                            .syncWith(KnockdownData.STREAM_CODEC, AttachmentSyncPredicate.all())
                            .persistent(KnockdownData.CODEC)
    );

    public static void register() {
        ModDataAttachments.KNOCKDOWN =new ModDataAttachments.DataAttachment<>(
                entity -> entity.getAttached(KNOCKDOWN_DATA),
                (entity, data) -> entity.setAttached(KNOCKDOWN_DATA, data),
                entity -> entity.hasAttached(KNOCKDOWN_DATA)
        );
    }
}
