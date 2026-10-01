package xyz.beefox.rotationlocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import xyz.beefox.rotationlocker.RotationLocker;

@Mixin(ItemPlacementContext.class)
public class ItemPlacementContextMixin{

    @Inject(method = "getPlayerLookDirection()Lnet/minecraft/util/math/Direction;", at = @At("TAIL"), cancellable = true)
    public void getPlayerLookDirectionInject(CallbackInfoReturnable<Direction> info){
        Direction lockDirection = RotationLocker.lockDirection;
        if(lockDirection != null){
            info.setReturnValue(lockDirection);
        }
    }

    @Inject(method = "getPlacementDirections()[Lnet/minecraft/util/math/Direction;", at = @At("HEAD"), cancellable = true)
    public void getVerticalPlayerLookDirection(CallbackInfoReturnable<Direction[]> info) {
        Direction lockDirection = RotationLocker.lockDirection;
        if (lockDirection != null) {
            if (lockDirection == Direction.DOWN || lockDirection == Direction.UP) {
                info.setReturnValue(new Direction[]{
                                lockDirection,
                                lockDirection.getOpposite(),
                                Direction.UP,
                                Direction.DOWN,
                                Direction.NORTH,
                                Direction.SOUTH,
                                Direction.WEST,
                                Direction.EAST
                        }
                        );
            }
        }
    }
}

