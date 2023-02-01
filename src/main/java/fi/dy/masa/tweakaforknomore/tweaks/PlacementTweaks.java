package fi.dy.masa.tweakaforknomore.tweaks;

import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.PositionUtils.HitPart;
import fi.dy.masa.malilib.util.restrictions.BlockRestriction;
import fi.dy.masa.tweakaforknomore.config.Configs;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakaforknomore.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlacementTweaks
{
    public static Direction tempDirection = null;
    public static BlockPos offsetPos = null;

    public static Direction directionHold = null;
    public static BlockPos offsetHold = null;
    public static boolean reverseHold = false;
    public static boolean intoHold = false;
    public static final BlockRestriction BLOCK_TYPE_RCLICK_RESTRICTION = new BlockRestriction();

    private static final Class<? extends Block>[] ACCURATE_AFTERCLICKER_BLOCKS = (Class<? extends Block>[]) new Class<?>[] {
            RepeaterBlock.class, ComparatorBlock.class
    };
    
    private static final int ACCURATE_RADIX = 16 * 2; // Multiplied by 2

    public static void holdSettings() {

        int count = 0;
        int reset = 0;

        if (tempDirection != null)
            count++;
        else if (directionHold != null)
            reset++;
        directionHold = tempDirection;

        if (offsetPos != null)
            count++;
        else if (offsetHold != null)
            reset++;
        offsetHold = offsetPos;

        boolean reverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();
        if (reverse)
            count++;
        else if (reverseHold)
            reset++;
        reverseHold = reverse;

        boolean into = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld();
        if (into)
            count++;
        else if (intoHold)
            reset++;
        intoHold = into;

        InfoUtils.printActionbarMessage("Holding " + count + " settings." + (reset > 0 ? (" Reset " + reset) : ""));
        
    }

    /**
     * this is a duplicate.
     * reason = private access
     */
    private static Direction getRotatedFacing(Direction originalSide, Direction playerFacingH, PositionUtils.HitPart hitPart) {
        if (originalSide.getAxis().isVertical()) {
            switch (hitPart) {
                case LEFT:
                    return playerFacingH.rotateYClockwise();
                case RIGHT:
                    return playerFacingH.rotateYCounterclockwise();
                case BOTTOM:
                    return originalSide == Direction.UP ? playerFacingH : playerFacingH.getOpposite();
                case TOP:
                    return originalSide == Direction.DOWN ? playerFacingH : playerFacingH.getOpposite();
                case CENTER:
                    return originalSide.getOpposite();
                default:
                    return originalSide;
            }
        } else {
            switch (hitPart) {
                case LEFT:
                    return originalSide.rotateYCounterclockwise();
                case RIGHT:
                    return originalSide.rotateYClockwise();
                case BOTTOM:
                    return Direction.UP;
                case TOP:
                    return Direction.DOWN;
                case CENTER:
                    return originalSide.getOpposite();
                default:
                    return originalSide;
            }
        }
    }

    public static Direction getScaffoldPlaceDirection(Direction side, HitPart hitPart, PlayerEntity player) {
            Direction offsetIn = getRotatedFacing(side, player.getHorizontalFacing(), hitPart).getOpposite();
          
            Direction extendDirection = null;
            if (side == Direction.UP || side == Direction.DOWN) {
                extendDirection = (hitPart == HitPart.CENTER || Configs.Generic.SCAFFOLD_PLACE_VANILLA.getBooleanValue()) ? player.getHorizontalFacing() : offsetIn;
            } else {
                extendDirection = (hitPart == HitPart.CENTER || Configs.Generic.SCAFFOLD_PLACE_VANILLA.getBooleanValue()) ? Direction.UP : offsetIn;
            }
	
            return extendDirection;
    }
    public static BlockPos getScaffoldPlacePosition(BlockPos pos, Direction extendDirection, World world, ItemStack stack, PlayerEntity player) {

        if (!(stack.getItem() instanceof BlockItem) || extendDirection == null) {
            return null;
        }

        Block itemBlock = ((BlockItem)stack.getItem()).getBlock();
        MinecraftClient mc = MinecraftClient.getInstance();
        double reach = mc.interactionManager.getReachDistance();
       
        BlockPos.Mutable tempPos = new BlockPos.Mutable(pos.getX(),pos.getY(),pos.getZ());
        for (int i = 0; i < Configs.Generic.SCAFFOLD_PLACE_DISTANCE.getIntegerValue(); i++) {
            tempPos.move(extendDirection);

            if (!MiscUtils.isInReach(tempPos, player, reach)) {
                return null;
            }

            BlockState state = world.getBlockState(tempPos);
            if (state.getBlock() != itemBlock) {
                if (state.isAir() || state.getMaterial().isReplaceable()) {
                    return tempPos.toImmutable();
                }
           
                return null;
            }
        }

        return null;
    }

    public static boolean canUseCarpetProtocolForAfterclicker(ItemStack stack) {
        Item item = stack.getItem();

        if (stack.isEmpty() || !(item instanceof BlockItem)) {
            return false;
        }

        Block block = ((BlockItem) item).getBlock();
        
        for (Class<? extends Block> c : ACCURATE_AFTERCLICKER_BLOCKS)
        {
            if (c.isInstance(block))
            {
                return true;
            }
        }
        return false;
    }

}
