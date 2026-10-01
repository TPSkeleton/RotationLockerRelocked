package xyz.beefox.rotationlocker.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.beefox.rotationlocker.RotationLocker;

@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {

	@Shadow
	@Mutable
	@Final
	private BlockHitResult hit;
	@Shadow
	@Mutable
	@Final
	private PlayerEntity player;


	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructorInjector(PlayerEntity player, Hand hand, BlockHitResult blockHit, CallbackInfo info) {

		Direction lockDirection = RotationLocker.lockDirection;
		if (lockDirection != null) {
			BlockPos newBlockPos = blockHit.getBlockPos().offset(blockHit.getSide());
			this.hit = blockHit.withSide(lockDirection).withBlockPos(newBlockPos);
		}
	}

	@Inject(method = "getPlayerFacing", at = @At("TAIL"), cancellable = true)
	public void facingInjector(CallbackInfoReturnable<Direction> info) {

		@Nullable
		Direction lockDirection = RotationLocker.lockDirection;

		if (lockDirection != null) {
			if (lockDirection.getAxis().isVertical() && player != null) {
				info.setReturnValue(player.getHorizontalFacing().getOpposite());
			} else {
				info.setReturnValue(lockDirection.getOpposite());
			}
		}
	}
}
