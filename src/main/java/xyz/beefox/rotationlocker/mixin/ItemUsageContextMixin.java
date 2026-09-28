package xyz.beefox.rotationlocker.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.beefox.rotationlocker.RotationLocker;

@Mixin(UseOnContext.class)
public class ItemUsageContextMixin {

	@Shadow @Mutable @Final private BlockHitResult hitResult;
	@Shadow @Mutable @Final private Player player;


	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructorInjector(Player player, InteractionHand hand, BlockHitResult blockHit, CallbackInfo info){

		Direction lockDirection = RotationLocker.lockDirection;
		if(lockDirection != null){
			BlockPos newBlockPos = blockHit.getBlockPos().relative(blockHit.getDirection());
			this.hitResult = blockHit.withDirection(lockDirection).withPosition(newBlockPos);
		}
	}

	@Inject(method = "getHorizontalDirection", at = @At("TAIL"), cancellable = true)
	public void facingInjector(CallbackInfoReturnable<Direction> info){

		@Nullable
		Direction lockDirection = RotationLocker.lockDirection;

		if(lockDirection != null){
			info.setReturnValue(lockDirection.getOpposite());
		}
	}
}