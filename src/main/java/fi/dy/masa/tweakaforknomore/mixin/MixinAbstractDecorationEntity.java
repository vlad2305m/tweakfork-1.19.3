package fi.dy.masa.tweakaforknomore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fi.dy.masa.tweakaforknomore.util.IDecorationEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractDecorationEntity.class)
public abstract class MixinAbstractDecorationEntity extends Entity implements IDecorationEntity {
    
    @Shadow private BlockPos attachmentPos;

	@Override
	public BlockPos getAttatched() {
		return attachmentPos;
	}

    public MixinAbstractDecorationEntity(EntityType<?> type, World world) {
        super(type, world);
    }
}