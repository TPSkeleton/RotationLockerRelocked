package xyz.beefox.rotationlocker;

import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RotationLocker implements ModInitializer {

	public static final net.minecraft.tag.TagKey<Item> LOCK_RESETERS = net.minecraft.tag.TagKey.of(
			Registry.ITEM_KEY,
			new Identifier("rotationlocker", "lock_reseters")
	);

	public static final Logger LOGGER = LoggerFactory.getLogger("rotationlocker");

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
								if(client.options.sneakKey.isPressed()){
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

						PlayerEntity player = MinecraftClient.getInstance().player;

						if(player != null) {

							if(lockDirection == Direction.UP && player.isHolding(stack -> stack.isIn(LOCK_RESETERS))) {
								lockDirection = null;
								client.player.sendMessage(new TranslatableText("text.rotationlocker.invalidlock").formatted(Formatting.DARK_RED), false);
							}
							if(lockDirection == Direction.DOWN && player.isHolding(stack -> stack.isIn(LOCK_RESETERS))) {
								lockDirection = null;
								client.player.sendMessage(new TranslatableText("text.rotationlocker.invalidlock").formatted(Formatting.DARK_RED), false);
							}
						}
					}
				}
		);
	}
}