package xyz.beefox.rotationlocker;

import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;



public class RotationLocker implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("rotationlocker");

	private static KeyBinding rotationLockKeyBinding;
	@Nullable
	public static Direction lockDirection;
	private static Boolean enableKey;

	@Override
	public void onInitialize() {



		rotationLockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.rotationlocker.lock",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				"category.rotationlocker"
		));

		enableKey = true;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
					if(client.player != null) {
						if (rotationLockKeyBinding.isPressed() && enableKey == true) {
							enableKey = false;
							if(lockDirection == null){
								lockDirection = Direction.getEntityFacingOrder(client.player)[0];
								client.player.sendMessage(new TranslatableText("text.rotationlocker.lock", lockDirection.asString()).formatted(Formatting.WHITE), false);
							} else {
								if(client.options.keySneak.isPressed()){
									lockDirection = lockDirection.getOpposite();
									client.player.sendMessage(new TranslatableText("text.rotationlocker.flip", lockDirection.asString()).formatted(Formatting.GRAY), false);
								} else {
									lockDirection = null;
									client.player.sendMessage(new TranslatableText("text.rotationlocker.unlock").formatted(Formatting.WHITE), false);
								}
							}
						}
						if (!rotationLockKeyBinding.isPressed() && enableKey == false){
							enableKey = true;
						}
					}
				}
		);
	}
}


