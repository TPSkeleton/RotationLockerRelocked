package xyz.beefox.rotationlocker;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
			new Identifier("rotationlocker", "lock_reseters")
	);

	public static final Logger LOGGER = LoggerFactory.getLogger("rotationlocker");

	private static KeyBinding rotationLockKeyBinding;
	@Nullable
	public static Direction lockDirection;
	private static Boolean enableKey;

	@Override
	public void onInitialize() {



		rotationLockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.rotationlocker.lock",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.rotationlocker"));

		enableKey = true;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			PlayerEntity player = MinecraftClient.getInstance().player;

			if(player != null) {
				if (player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getRegistryEntry())) &&
						rotationLockKeyBinding.isPressed() && enableKey == true) {
					if (Direction.getEntityFacingOrder(client.player)[0] == Direction.DOWN ||
							Direction.getEntityFacingOrder(client.player)[0] == Direction.UP) {
						enableKey = false;
						lockDirection = null;
						client.player.sendMessage(Text.translatable("text.rotationlocker.invalidlock").formatted(Formatting.DARK_RED), false);
						client.player.playSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.MASTER, 1.2F, 0.2F);
					}
				}
			}
			if(client.player != null) {
				if (rotationLockKeyBinding.isPressed() && enableKey == true) {
					enableKey = false;
					if(lockDirection == null){
						lockDirection = Direction.getEntityFacingOrder(client.player)[0];
						client.player.sendMessage(Text.translatable("text.rotationlocker.lock", lockDirection.asString()).formatted(Formatting.WHITE), false);
						client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.20F, 1.0F);
					} else {
						if(client.options.sneakKey.isPressed()){
							lockDirection = lockDirection.getOpposite();
							client.player.sendMessage(Text.translatable("text.rotationlocker.flip", lockDirection.asString()).formatted(Formatting.GRAY));
							client.player.playSound(SoundEvents.UI_TOAST_IN, SoundCategory.MASTER, 6.0F, 0.75F);
						} else {
							lockDirection = null;
							client.player.sendMessage(Text.translatable("text.rotationlocker.unlock").formatted(Formatting.WHITE), false);
							client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.15F, 0.65F);
						}
						}
				}
				if (!rotationLockKeyBinding.isPressed() && enableKey == false){
					enableKey = true;
					}
				}
			if (lockDirection == Direction.DOWN && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getRegistryEntry())) ||
					lockDirection == Direction.UP && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getRegistryEntry()))) {
						lockDirection = null;
						// a
				}
			}
		);
	}
}