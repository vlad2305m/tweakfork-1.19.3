package fi.dy.masa.tweakaforknomore.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Direction;

public class MiscUtils
{
    public static Vec3d getEyesPos(PlayerEntity player)
	{	
		return new Vec3d(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ());
	}
    public static BlockPos getPlayerHeadPos(PlayerEntity player)
	{	
		return (player.getPose() == EntityPose.STANDING) ? player.getBlockPos().offset(Direction.UP) : player.getBlockPos();
	}
    public static boolean isInReach(BlockPos pos, PlayerEntity player, double reach) {
        Vec3d playerpos = getEyesPos(player);
		double d = playerpos.getX() - ((double)pos.getX() + 0.5D);
		double d1 = playerpos.getY() - ((double)pos.getY() + 0.5D);
		double d2 = playerpos.getZ() - ((double)pos.getZ() + 0.5D);
        return d*d+d1*d1+d2*d2 <= reach*reach;
    }
}
