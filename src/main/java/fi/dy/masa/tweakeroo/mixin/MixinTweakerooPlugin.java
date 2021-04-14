package fi.dy.masa.tweakeroo.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import fi.dy.masa.tweakeroo.util.PistonUtils;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;

public class MixinTweakerooPlugin implements IMixinConfigPlugin
{
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		// only loads our pushLimit modifier when carpet mod not loaded
		if (mixinClassName.endsWith(".MixinPistonHandler"))
		{
			return PistonUtils.loadVanilla();
		}
		

		return true;
	}

	// dummy stuffs down below

	@Override
	public void onLoad(String mixinPackage)
	{

	}

	@Override
	public String getRefMapperConfig()
	{
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			Mixins.addConfiguration("mixins.optifine_patch.json");
		}
	}

	@Override
	public List<String> getMixins()
	{
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}
}
