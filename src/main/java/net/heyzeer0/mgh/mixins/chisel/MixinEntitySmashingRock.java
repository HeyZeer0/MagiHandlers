package net.heyzeer0.mgh.mixins.chisel;

import net.heyzeer0.mgh.hacks.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by Frani on 18/09/2017.
 */

@Pseudo
@Mixin(targets = "team/chisel/entity/EntitySmashingRock", remap = false)
public abstract class MixinEntitySmashingRock extends EntityThrowable {

    public MixinEntitySmashingRock(World w) {
        super(w);
    }

    @Redirect(method = "smash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z"))
    public boolean injectSetBlock(World world, int x, int y, int z, Block block, int meta, int flag) {
        if (getThrower() instanceof EntityPlayer) {
            return BlockHelper.setBlockWithOwner(x, y, z, block, meta, flag, world, (EntityPlayer)getThrower());
        }
        return false;
    }

}
