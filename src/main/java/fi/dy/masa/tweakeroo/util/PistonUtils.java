/*
    Based on Fallen Breath's pistorder mod

    https://github.com/Fallen-Breath/pistorder


    Pistorder License:

    GNU GENERAL PUBLIC LICENSE

    Read more here:
    https://github.com/Fallen-Breath/pistorder/blob/1.15.2-fabric/LICENSE

*/

package fi.dy.masa.tweakeroo.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import carpet.CarpetSettings;
import fi.dy.masa.tweakeroo.renderer.OverlayRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import quickcarpet.settings.Settings;

public class PistonUtils {
 
    private static final int MAX_PUSH_LIMIT_FOR_CALC = 128;
   
    public static HashMap<Long, PistonSource> sources = new HashMap<Long, PistonSource>();
    public static HashMap<Long, PistonInfo> hotBlocks = new HashMap<Long, PistonInfo>();
    public static boolean overridePushLimit = false;
    public static int lastId = 0;

    private static int VanillaPushLimit = 12;


    public static boolean recalculate_flag = false;

    public static void clearAll() {
        sources.clear();
        hotBlocks.clear();
        overridePushLimit = false;
    }
    public static boolean toggleAtPos(World world, BlockPos pos, Direction direction, boolean isPush)
	{
        long key = pos.asLong();
        PistonSource source = sources.get(key);

        if (source == null) {
            PistonSource newSource = new PistonSource(world, pos, direction, isPush, world.getBlockState(pos));
            sources.put(key,newSource);
            newSource.calculatePush();
            return true;
        } else {
            if (source.isPush == isPush && source.pushDirection.equals(direction)) {
                sources.remove(key);
                recalculate_flag = true;
                return false;
            } else {
                PistonSource newSource = new PistonSource(world, pos, direction, isPush, world.getBlockState(pos));
                sources.put(key,newSource);
                recalculate_flag = true;
                return true;
            }

        }
    }
    public static boolean loadVanilla() {
        return !FabricLoader.getInstance().isModLoaded("carpet") && !FabricLoader.getInstance().isModLoaded("quickcarpet");
    }

    public static int pushLimitCarpet(int value) {
        if (value > 0) CarpetSettings.pushLimit = value;
        return CarpetSettings.pushLimit;
    }
    public static int pushLimitQuickCarpet(int value) {
        if (value > 0) Settings.pushLimit = value;
        return Settings.pushLimit;
    }
    public static int getPushLimit() {

        
        // carpet
        try {
            return pushLimitCarpet(-1);
        } catch (Exception e) {

        }

        // quickcarpet
        try {
            return pushLimitQuickCarpet(-1);
        } catch (Exception e) {

        }
        
        return VanillaPushLimit;

    }
    public static int setPushLimit(int value) {

       
        // carpet
        try {
           return pushLimitCarpet(value);
        } catch (Exception e) {

        }

        // quickcarpet
        try {
            return pushLimitQuickCarpet(value);
        } catch (Exception e) {

        }
        
        return VanillaPushLimit = value;

    }

    public static void renderOverlay() {
        if (recalculate_flag) {
            recalculate();
            recalculate_flag = false;
        } else
            recalculate_flag = true;

        MinecraftClient mc = MinecraftClient.getInstance();
        
       
        for (PistonInfo info : hotBlocks.values()) {
            
            switch (info.type) {
                case 0:  // piston source
                    String actionResult = info.source.moveSuccess ? Formatting.GREEN + "√" : Formatting.RED + "×";
                    OverlayRenderer.drawString(String.format("%s %s", info.source.isPush ? "Push" : "Pull", actionResult), info.pos, Formatting.GOLD.getColorValue(), -0.5F);
                    OverlayRenderer.drawString(info.source.blockCount + " blocks", info.pos, Formatting.GOLD.getColorValue(), 0.5F);

                break;
                case 1: // moving
                     OverlayRenderer.drawString(String.valueOf(info.order), info.pos, Formatting.WHITE.getColorValue(), 0);
     
                break;
                case 2: // breaking
                     OverlayRenderer.drawString(String.valueOf(info.order), info.pos, Formatting.RED.getColorValue() | (0xFF << 24), 0);
                break;
            }
        }
        OverlayRenderer.renderPistonGroups(mc, hotBlocks);
    
    }

    @Deprecated
    public static void updateBlock(BlockPos pos) {

        long key = pos.asLong();
        if (sources.containsKey(key)) {
            sources.remove(key);
            recalculate_flag = true;
            return;
        }
        if (hotBlocks.containsKey(key)) {
            recalculate_flag = true;
            return;
        }
        if (
            hotBlocks.containsKey(pos.offset(Direction.UP).asLong()) || 
            hotBlocks.containsKey(pos.offset(Direction.DOWN).asLong()) ||
            hotBlocks.containsKey(pos.offset(Direction.EAST).asLong()) ||
            hotBlocks.containsKey(pos.offset(Direction.WEST).asLong()) ||
            hotBlocks.containsKey(pos.offset(Direction.NORTH).asLong()) ||
            hotBlocks.containsKey(pos.offset(Direction.SOUTH).asLong())
        ) {
            recalculate_flag = true;
            return;
        }
    }
    public static void recalculate() {
        hotBlocks.clear();
        for (PistonSource temp : sources.values()) {

            if (!temp.checkValid()) {
                sources.remove(temp.pos.asLong());
            } else
                temp.calculatePush();
        }
        recalculate_flag = false;
    }

    public static class PistonSource {
        public int id;
        public BlockPos pos;
        public Direction pushDirection;
        public boolean isPush;

        public boolean moveSuccess;
        public int blockCount = 0;

        private PistonHandler pistonHandler;
        private World world;

        public BlockState current;
        


        PistonSource(World world, BlockPos pos, Direction pushDirection,boolean isPush, BlockState current) {
            this.pos = pos;
            this.pushDirection = pushDirection;
            this.isPush = isPush;
            this.world = world;
            this.pistonHandler = new PistonHandler(world, pos, pushDirection, isPush);
            this.id = lastId++;
            this.current = current;
        }
        public boolean checkValid() {
            BlockState blockState = this.world.getBlockState(pos);
            if (blockState != current) {
                if (blockState.getBlock() instanceof PistonBlock) {
                    Direction dir = blockState.get(Properties.FACING);
                    boolean extended = blockState.get(PistonBlock.EXTENDED);

                    if (dir != this.pushDirection) return false;

                    this.isPush = !extended;
                    return true;
                }
                return false;
            }
            return true;
        }
        public void calculatePush() {
            BlockState[] states = new BlockState[2];
            if (!isPush)
			{
				states[0] = world.getBlockState(pos);  // piston base
				states[1] = world.getBlockState(pos.offset(pushDirection));  // piston head
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 18);
				world.setBlockState(pos.offset(pushDirection), Blocks.AIR.getDefaultState(), 18);
			}
          
            this.moveSuccess = pistonHandler.calculatePush();

            int lastValue = getPushLimit();
            if (!this.moveSuccess)
			{
                setPushLimit(MAX_PUSH_LIMIT_FOR_CALC);
				pistonHandler.calculatePush();
			}
           
            List<BlockPos> brokenBlocks = pistonHandler.getBrokenBlocks();
            List<BlockPos> movedBlocks = pistonHandler.getMovedBlocks();
            setPushLimit(lastValue);
            if (!isPush)
			{
				world.setBlockState(pos, states[0], 18);
				world.setBlockState(pos.offset(pushDirection), states[1], 18);
            }
            
            Collections.reverse(brokenBlocks);
            Collections.reverse(movedBlocks);
            
            hotBlocks.put(this.pos.asLong(),new PistonInfo(this,pos,0,0));

            for (int i = 0; i < movedBlocks.size(); i++)
			{
                BlockPos p = movedBlocks.get(i);
				hotBlocks.put(p.asLong(),new PistonInfo(this,p,i + 1,1));
            }

            blockCount = movedBlocks.size();

            for (int i = 0; i < brokenBlocks.size(); i++)
			{
                BlockPos p = brokenBlocks.get(i);
				hotBlocks.put(p.asLong(),new PistonInfo(this,p,i + 1,2));
			}
			
        }
       


    }

    public static class PistonInfo {
        public PistonSource source;
        public BlockPos pos;
        public int order;
        public int type;
        PistonInfo(PistonSource source, BlockPos pos, int order, int type) {
            this.source = source;
            this.pos = pos;
            this.order = order;
            this.type = type;
        }
    }

}
