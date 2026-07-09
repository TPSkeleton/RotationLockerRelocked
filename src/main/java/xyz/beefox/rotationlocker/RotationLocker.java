package xyz.beefox.rotationlocker;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RotationLocker implements ModInitializer {

	public static final TagKey<Item> LOCK_RESETERS = TagKey.of(
			RegistryKeys.ITEM,
			Identifier.of("rotationlocker", "lock_reseters")
	);

	public static final String MOD_ID = "rotationlocker";
	public static final Logger LOGGER = LoggerFactory.getLogger("rotationlocker");

	private static KeyBinding rotationLockKeyBinding;
	@Nullable
	public static Direction lockDirection;
	private static Boolean enableKey;

	@Override
	public void onInitialize() {



		rotationLockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.rotationlocker.lock", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_K, // The keycode of the key
				KeyBinding.Category.create(Identifier.of(MOD_ID))
		));
				// "category.rotationlocker" // The translation key of the keybinding's category.

		;

		enableKey = true;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.player != null) {
				if (rotationLockKeyBinding.isPressed() && enableKey == true) {
					enableKey = false;
					if(lockDirection == null){
						lockDirection = Direction.getEntityFacingOrder(client.player)[0];
						client.player.sendMessage(Text.translatable("text.rotationlocker.lock", lockDirection.asString()).formatted(Formatting.WHITE), false);
					} else {
						if(client.options.sneakKey.isPressed()){
							lockDirection = lockDirection.getOpposite();
							client.player.sendMessage(Text.translatable("text.rotationlocker.flip", lockDirection.asString()).formatted(Formatting.GRAY), false);
						} else {
							lockDirection = null;
							client.player.sendMessage(Text.translatable("text.rotationlocker.unlock").formatted(Formatting.WHITE), false);
						}
						}
				}
				if (!rotationLockKeyBinding.isPressed() && enableKey == false){
					enableKey = true;
				}

				PlayerEntity player = MinecraftClient.getInstance().player;

		if(player != null) {

			if(lockDirection == Direction.UP && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getRegistryEntry()))) {
				lockDirection = null;
				client.player.sendMessage(Text.translatable("text.rotationlocker.invalidlock").formatted(Formatting.DARK_RED), false);
			}
			if(lockDirection == Direction.DOWN && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getRegistryEntry()))) {
				lockDirection = null;
				client.player.sendMessage(Text.translatable("text.rotationlocker.invalidlock").formatted(Formatting.DARK_RED), false);
						}
					}
				}
			}
		);
	}
}