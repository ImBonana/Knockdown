package me.imbanana.knockdown;

import me.imbanana.knockdown.mixin.PlayerMixin;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KnockdownMod {
    public static final String MOD_ID = "knockdown";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
    }

    public static Identifier idOf(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
