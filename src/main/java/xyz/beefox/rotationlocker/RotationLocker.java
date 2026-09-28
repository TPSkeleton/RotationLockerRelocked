package xyz.beefox.rotationlocker;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RotationLocker implements ModInitializer {

	public static final TagKey<Item> LOCK_RESETERS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath("rotationlocker", "lock_reseters")
	);

	public static final String MOD_ID = "rotationlocker";
	public static final Logger LOGGER = LoggerFactory.getLogger("rotationlocker");

	private static KeyMapping rotationLockKeyBinding;
	@Nullable
	public static Direction lockDirection;
	private static Boolean enableKey;

	@Override
	public void onInitialize() {

		rotationLockKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.rotationlocker.lock",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				KeyMapping.Category.register(Identifier.parse(MOD_ID))));

		enableKey = true;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
					if(client.player != null) {
						if (rotationLockKeyBinding.isDown() && enableKey == true) {
							enableKey = false;
							if(lockDirection == null){
								lockDirection = Direction.orderedByNearest(client.player)[0];
								client.player.sendSystemMessage(Component.translatable("text.rotationlocker.lock", lockDirection.getSerializedName()).withStyle(ChatFormatting.WHITE));
							} else {
								if(client.options.keyShift.isDown()){
									lockDirection = lockDirection.getOpposite();
									client.player.sendSystemMessage(Component.translatable("text.rotationlocker.flip", lockDirection.getSerializedName()).withStyle(ChatFormatting.GRAY));
								} else {
									lockDirection = null;
									client.player.sendSystemMessage(Component.translatable("text.rotationlocker.unlock").withStyle(ChatFormatting.WHITE));
								}
							}
						}
						if (!rotationLockKeyBinding.isDown() && enableKey == false){
							enableKey = true;
						}

						Player player = Minecraft.getInstance().player;

						if(player != null) {

							if(lockDirection == Direction.UP && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getItem().builtInRegistryHolder()))) {
								lockDirection = null;
								client.player.sendSystemMessage(Component.translatable("text.rotationlocker.invalidlock").withStyle(ChatFormatting.DARK_RED));
							}
							if(lockDirection == Direction.DOWN && player.isHolding(stack -> ClientTags.isInWithLocalFallback(LOCK_RESETERS, stack.getItem().builtInRegistryHolder()))) {
								lockDirection = null;
								client.player.sendSystemMessage(Component.translatable("text.rotationlocker.invalidlock").withStyle(ChatFormatting.DARK_RED));
							}
						}
					}
				}
		);
	}
}