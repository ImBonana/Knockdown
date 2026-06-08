package me.imbanana.knockdown.util;

import net.minecraft.network.chat.Component;

public interface IKnockdownable {
    void knockdown();
    void setDeathMessage(Component message);
    Component getDeathMessage();
    boolean isKnockedDown();
    boolean shouldKnockdown();
    int getTicksLeft();
    void setTicksLeft(int value);
    int getMaxTicks();
    int getBleedOutSpeed();
    void setFastBleedOut(boolean value);
    boolean isBleedingOutFast();
    void setWaitingForHelp(boolean value);
    boolean isWaitingForHelp();
    void syncTicksLeft();
}
