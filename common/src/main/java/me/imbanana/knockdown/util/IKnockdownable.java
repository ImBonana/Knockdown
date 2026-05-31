package me.imbanana.knockdown.util;

import net.minecraft.network.chat.Component;

public interface IKnockdownable {
    void knockdown();
    void setDeathMessage(Component message);
    Component getDeathMessage();
    boolean isKnockedDown();
    boolean shouldKnockdown();
}
