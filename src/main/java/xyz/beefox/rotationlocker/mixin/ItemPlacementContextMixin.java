package xyz.beefox.rotationlocker.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.beefox.rotationlocker.RotationLocker;

@Mixin(BlockPlaceContext.class)
public class ItemPlacementContextMixin{
    
    @Inject(method = "getNearestLookingDirection()Lnet/minecraft/core/Direction;", at = @At("TAIL"), cancellable = true)
    public void getPlayerLookDirectionInject(CallbackInfoReturnable<Direction> info){
        Direction lockDirection = RotationLocker.lockDirection;
        if(lockDirection != null){
            info.setReturnValue(lockDirection);
        }
    }

    @Inject(method = "getNearestLookingVerticalDirection()Lnet/minecraft/core/Direction;", at = @At("TAIL"), cancellable = true)
    public void getVerticalPlayerLookDirection(CallbackInfoReturnable<Direction> info){
        Direction lockDirection = RotationLocker.lockDirection;
        if(lockDirection != null){
            if(lockDirection == Direction.DOWN){
                info.setReturnValue(Direction.DOWN);
            } else {
                info.setReturnValue(Direction.UP);
            }
        }        
    }
}

