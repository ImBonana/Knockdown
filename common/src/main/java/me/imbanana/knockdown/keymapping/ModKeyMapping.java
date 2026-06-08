package me.imbanana.knockdown.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModKeyMapping {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(KnockdownMod.idOf("knockdown"));

    public static KeyMapping fastBleedOutKey;

    public static void register(Function<KeyMapping, KeyMapping> registrar) {
        fastBleedOutKey = registrar.apply(new KeyMapping(
                createTranslationKey("fast_knockout"),
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                CATEGORY

        ));
    }

    public static void tick(Minecraft client) {
        if (client.player == null) return;

        IKnockdownable player = (IKnockdownable) client.player;

        if (!fastBleedOutKey.isDown() && player.isBleedingOutFast()) {
            player.setFastBleedOut(false);
        }

        while (fastBleedOutKey.consumeClick()) {
            if (player.isKnockedDown()) {
                player.setFastBleedOut(true);
            }
        }
    }

    private static String createTranslationKey(String name) {
        return "keu." + KnockdownMod.MOD_ID + "." + name;
    }
}
