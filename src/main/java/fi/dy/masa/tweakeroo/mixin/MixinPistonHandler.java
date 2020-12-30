package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.util.PistonUtils;
import net.minecraft.block.piston.PistonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PistonHandler.class)
public abstract class MixinPistonHandler
{
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12), require = 3, allow = 3)
	private int modifyPushLimitPistorderMod(int value)
	{
		return PistonUtils.getPushLimit();
	}
}
