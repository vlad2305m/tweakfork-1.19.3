package fi.dy.masa.tweakeroo.tweaks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.mixin.MixinPistonBlock;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RenderTweaks {

    private static ConcurrentHashMap<Long, ListMapEntry> SELECTIVE_BLACKLIST = new ConcurrentHashMap<Long, ListMapEntry>();
    private static ConcurrentHashMap<Long, ListMapEntry> SELECTIVE_WHITELIST = new ConcurrentHashMap<Long, ListMapEntry>();

    private static Color4f colorPos1 = new Color4f(1f, 0.0625f, 0.0625f);
    private static Color4f colorPos2 = new Color4f(0.0625f, 0.0625f, 1f);
    private static Color4f sideColor = Color4f.fromColor(0x30FFFFFF);
    private static Color4f colorOverlapping = new Color4f(1f, 0.0625f, 1f);
    private static Color4f colorX = new Color4f(1f, 0.25f, 0.25f);
    private static Color4f colorY = new Color4f(0.25f, 1f, 0.25f);
    private static Color4f colorZ = new Color4f(0.25f, 0.25f, 1f);
    private static Color4f colorLooking = new Color4f(1.0f, 1.0f, 1.0f, 0.6f);
    private static Color4f colorWhitelist = new Color4f(0.1f, 0.7f, 0.1f, 0.25f);
    private static Color4f colorBlacklist = new Color4f(0.7f, 0.1f, 0.1f, 0.25f);

    public static Selection AREA_SELECTION = new Selection();

    public static BlockPos posLookingAt = null;

    public static void onTick() {
        // Dumb rendundancy due to replaymod
        MinecraftClient mc = MinecraftClient.getInstance();
        if (FeatureToggle.TWEAK_AREA_SELECTOR.getBooleanValue()) {
            if (mc.options.keyAttack.isPressed()) {
                select(false);
            }

            if (mc.options.keyUse.isPressed()) {
                select(true);
            }
        }

    }

    public static void render(MatrixStack matrices, float partialTicks) {

        if (FeatureToggle.TWEAK_AREA_SELECTOR.getBooleanValue()
                || FeatureToggle.TWEAK_SELECTIVE_BLOCKS_RENDER_OUTLINE.getBooleanValue()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            float expand = 0.001f;
            float lineWidthBlockBox = 2f;
            float lineWidthArea = 1.5f;

            if (FeatureToggle.TWEAK_AREA_SELECTOR.getBooleanValue())
                updateLookingAt();

            RenderSystem.pushMatrix();
            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
            fi.dy.masa.malilib.render.RenderUtils.setupBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.disableLighting();
            // RenderSystem.depthMask(false);
            RenderSystem.disableTexture();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01F);

            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(-1.2f, -0.2f);

            if (FeatureToggle.TWEAK_SELECTIVE_BLOCKS_RENDER_OUTLINE.getBooleanValue()) {
                renderLists(matrices);
            }
            if (FeatureToggle.TWEAK_AREA_SELECTOR.getBooleanValue()) {
                if (posLookingAt != null) {
                    RenderUtils.renderBlockOutline(posLookingAt, expand, lineWidthBlockBox, colorLooking, mc);
                }
                renderSelection(matrices, AREA_SELECTION);
            }

            RenderSystem.polygonOffset(0f, 0f);
            RenderSystem.disablePolygonOffset();
            RenderSystem.popMatrix();
            RenderSystem.enableTexture();
            // RenderSystem.depthMask(true);

        }
    }

    private static void renderLists(MatrixStack matrices) {
        float expand = 0.001f;
        float lineWidthBlockBox = 2f;
        float lineWidthArea = 1.5f;
        MinecraftClient mc = MinecraftClient.getInstance();

        for (ListMapEntry entry : SELECTIVE_BLACKLIST.values()) {
            RenderUtils.renderBlockOutline(entry.currentPosition, expand, lineWidthBlockBox, colorBlacklist, mc);
        }
        for (ListMapEntry entry : SELECTIVE_WHITELIST.values()) {
            RenderUtils.renderBlockOutline(entry.currentPosition, expand, lineWidthBlockBox, colorWhitelist, mc);
        }
    }

    public static void updateLookingAt() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.crosshairTarget != null && mc.crosshairTarget instanceof BlockHitResult) {
            posLookingAt = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

            // use offset
            if (Hotkeys.AREA_SELECTION_OFFSET.getKeybind().isKeybindHeld())
                posLookingAt = posLookingAt.offset(((BlockHitResult) mc.crosshairTarget).getSide());
        } else {
            posLookingAt = null;
        }
    }

    public static void select(boolean pos2) {
        if (posLookingAt == null)
            return;
        if (pos2) {
            AREA_SELECTION.pos2 = posLookingAt;
        } else {
            AREA_SELECTION.pos1 = posLookingAt;
        }
    }

    public static void addSelectionToList() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null)
            return;
        if (AREA_SELECTION.pos1 == null || AREA_SELECTION.pos2 == null) {
            InfoUtils.printActionbarMessage("Please set an area first");
            return;
        }
        ListType type = (ListType) Configs.Lists.SELECTIVE_BLOCKS_LIST_TYPE.getOptionListValue();

        if (type == ListType.NONE) {
            InfoUtils.printActionbarMessage("No list selected");
            return;
        }

        Iterator<BlockPos> iterator = BlockPos.iterate(AREA_SELECTION.pos1, AREA_SELECTION.pos2).iterator();
        int count = 0;
        ConcurrentHashMap<Long, ListMapEntry> list = (type == ListType.WHITELIST) ? SELECTIVE_WHITELIST
                : SELECTIVE_BLACKLIST;

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next().toImmutable();

            if (Configs.Generic.AREA_SELECTION_USE_ALL.getBooleanValue() || !mc.world.getBlockState(pos).isAir()) {
                if (!list.containsKey(pos.asLong())) {
                    list.put(pos.asLong(), new ListMapEntry(pos));
                    count++;
                }
            }
        }
        rebuildStrings();
        InfoUtils.printActionbarMessage("Added " + count + " blocks");
    }

    public static void removeSelectionFromList() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null)
            return;
        if (AREA_SELECTION.pos1 == null || AREA_SELECTION.pos2 == null) {
            InfoUtils.printActionbarMessage("Please set an area first");
            return;
        }
        ListType type = (ListType) Configs.Lists.SELECTIVE_BLOCKS_LIST_TYPE.getOptionListValue();

        if (type == ListType.NONE) {
            InfoUtils.printActionbarMessage("No list selected");
            return;
        }

        Iterator<BlockPos> iterator = BlockPos.iterate(AREA_SELECTION.pos1, AREA_SELECTION.pos2).iterator();
        int count = 0;
        ConcurrentHashMap<Long, ListMapEntry> list = (type == ListType.WHITELIST) ? SELECTIVE_WHITELIST
                : SELECTIVE_BLACKLIST;

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (list.containsKey(pos.asLong())) {
                list.remove(pos.asLong());
                count++;
            }

        }
        rebuildStrings();
        InfoUtils.printActionbarMessage("Removed " + count + " blocks");
    }

    // From litematica
    public static void renderSelection(MatrixStack matrices, Selection selection) {

        BlockPos pos1 = selection.pos1;
        BlockPos pos2 = selection.pos2;
        if (pos1 == null && pos2 == null) {
            return;
        }
        float expand = 0.001f;
        float lineWidthBlockBox = 2f;
        float lineWidthArea = 1.5f;

        MinecraftClient mc = MinecraftClient.getInstance();

        if (pos1 != null && pos2 != null) {
            if (pos1.equals(pos2) == false) {
                RenderUtils.renderAreaOutlineNoCorners(pos1, pos2, lineWidthArea, colorX, colorY, colorZ, mc);

                RenderUtils.renderAreaSides(pos1, pos2, sideColor, matrices, mc);

                RenderUtils.renderBlockOutline(pos1, expand, lineWidthBlockBox, colorPos1, mc);
                RenderUtils.renderBlockOutline(pos2, expand, lineWidthBlockBox, colorPos2, mc);
            } else {
                RenderUtils.renderBlockOutlineOverlapping(pos1, expand, lineWidthBlockBox, colorPos1, colorPos2,
                        colorOverlapping, matrices, mc);
            }
        } else {
            if (pos1 != null) {
                RenderUtils.renderBlockOutline(pos1, expand, lineWidthBlockBox, colorPos1, mc);
            }

            if (pos2 != null) {
                RenderUtils.renderBlockOutline(pos2, expand, lineWidthBlockBox, colorPos2, mc);
            }
        }

    }

    public static void onPistonEvent(BlockState state, World world, BlockPos pos, int type, int data) {

        if (!Configs.Generic.SELECTIVE_BLOCKS_TRACK_PISTONS.getBooleanValue()
                || (!FeatureToggle.TWEAK_SELECTIVE_BLOCKS_RENDERING.getBooleanValue() && !FeatureToggle.TWEAK_SELECTIVE_BLOCKS_RENDER_OUTLINE.getBooleanValue())
                || (SELECTIVE_WHITELIST.size() == 0 && SELECTIVE_BLACKLIST.size() == 0))
            return;

        if (type == 2)
            return;

        Direction pushDirection = Direction.byId(data & 7);

        PistonHandler pistonHandler = new PistonHandler(world, pos, pushDirection, type == 0);

        BlockState state2 = null;

        if (type != 0 && !((MixinPistonBlock) state.getBlock()).getSticky())
            return; // non sticky pistons do nothing

        if (type != 0) {

            state2 = world.getBlockState(pos.offset(pushDirection)); // piston head
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 18);
            world.setBlockState(pos.offset(pushDirection), Blocks.AIR.getDefaultState(), 18);
        }
        boolean moveSuccess = pistonHandler.calculatePush();

        if (type != 0) {
            world.setBlockState(pos, state, 18);
            world.setBlockState(pos.offset(pushDirection), state2, 18);
        }

        
        boolean attatchedWhitelist = SELECTIVE_WHITELIST
                .containsKey(pos.offset(pushDirection, (type == 0) ? 1 : 2).asLong());
        boolean attatchedBlacklist = SELECTIVE_BLACKLIST
                .containsKey(pos.offset(pushDirection, (type == 0) ? 1 : 2).asLong());

        boolean whitelisted = SELECTIVE_WHITELIST.containsKey(pos.asLong());
        boolean blacklisted = SELECTIVE_BLACKLIST.containsKey(pos.asLong());

        if (type == 0) { // extending
            if (whitelisted) {
            if (attatchedWhitelist) {
                SELECTIVE_WHITELIST.get(pos.offset(pushDirection).asLong()).preserve = true;
            } else {
                SELECTIVE_WHITELIST.put(pos.offset(pushDirection, 1).asLong(),
                        new ListMapEntry(pos.offset(pushDirection, 1)));
            }
            }
            if (blacklisted) {
            if (attatchedBlacklist) {
                SELECTIVE_BLACKLIST.get(pos.offset(pushDirection).asLong()).preserve = true;
            } else {
                SELECTIVE_BLACKLIST.put(pos.offset(pushDirection, 1).asLong(),
                        new ListMapEntry(pos.offset(pushDirection, 1)));
            }
            }
        }
        if (moveSuccess) {
            // List<BlockPos> brokenBlocks = pistonHandler.getBrokenBlocks();
            List<BlockPos> movedBlocks = pistonHandler.getMovedBlocks();

            ArrayList<ListMapEntry> toMoveWhitelist = new ArrayList<ListMapEntry>();
            ArrayList<ListMapEntry> toMoveBlacklist = new ArrayList<ListMapEntry>();

            ArrayList<ListMapEntry> toAddWhitelist = new ArrayList<ListMapEntry>();
            ArrayList<ListMapEntry> toAddBlacklist = new ArrayList<ListMapEntry>();

            for (BlockPos p : movedBlocks) {
                long key = p.asLong();
                if (SELECTIVE_WHITELIST.containsKey(key)) {
                    ListMapEntry entry = SELECTIVE_WHITELIST.get(key);
                    toMoveWhitelist.add(entry);

                    SELECTIVE_WHITELIST.remove(key);
                    if (entry.preserve) {
                        entry.preserve = false;
                        toAddWhitelist.add(new ListMapEntry(p));
                    }
                }

                if (SELECTIVE_BLACKLIST.containsKey(key)) {
                    ListMapEntry entry = SELECTIVE_BLACKLIST.get(key);
                    toMoveBlacklist.add(entry);

                    SELECTIVE_BLACKLIST.remove(key);
                    if (entry.preserve) {
                        entry.preserve = false;
                        toAddBlacklist.add(new ListMapEntry(p));
                    }
                }
            }

            for (ListMapEntry p : toMoveWhitelist) {
                p.currentPosition = p.currentPosition.offset(pushDirection, (type == 0) ? 1 : -1);
                if (SELECTIVE_WHITELIST.containsKey(p.currentPosition.asLong()))
                    p.preserve = true;
                SELECTIVE_WHITELIST.put(p.currentPosition.asLong(), p);
            }

            for (ListMapEntry p : toMoveBlacklist) {
                p.currentPosition = p.currentPosition.offset(pushDirection, (type == 0) ? 1 : -1);
                if (SELECTIVE_BLACKLIST.containsKey(p.currentPosition.asLong()))
                    p.preserve = true;
                SELECTIVE_BLACKLIST.put(p.currentPosition.asLong(), p);
            }

            for (ListMapEntry p : toAddWhitelist) {
                SELECTIVE_WHITELIST.put(p.currentPosition.asLong(), p);
            }

            for (ListMapEntry p : toAddBlacklist) {
                SELECTIVE_BLACKLIST.put(p.currentPosition.asLong(), p);
            }
        }
    }

    public static boolean isPositionValidForRendering(BlockPos pos) {
        return isPositionValidForRendering(pos.asLong());
    }

    public static boolean isPositionValidForRendering(long key) {
        if (!FeatureToggle.TWEAK_SELECTIVE_BLOCKS_RENDERING.getBooleanValue()) {
            return true;
        }

        switch ((ListType) Configs.Lists.SELECTIVE_BLOCKS_LIST_TYPE.getOptionListValue()) {
            case NONE:
                return true;
            case WHITELIST:
                return SELECTIVE_WHITELIST.containsKey(key);
            case BLACKLIST:
                return !SELECTIVE_BLACKLIST.containsKey(key);
        }

        return false;
    }

    public static void rebuildLists() {
        SELECTIVE_BLACKLIST.clear();
        SELECTIVE_WHITELIST.clear();
        putMapFromString(SELECTIVE_BLACKLIST, Configs.Lists.SELECTIVE_BLOCKS_BLACKLIST.getStringValue());
        putMapFromString(SELECTIVE_WHITELIST, Configs.Lists.SELECTIVE_BLOCKS_WHITELIST.getStringValue());

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.world != null && mc.worldRenderer != null) {
            if (mc.world.getLightingProvider() != null) {
                for (ListMapEntry entry : SELECTIVE_BLACKLIST.values()) {
                    mc.world.getLightingProvider().checkBlock(entry.currentPosition);
                }
                for (ListMapEntry entry : SELECTIVE_WHITELIST.values()) {
                    mc.world.getLightingProvider().checkBlock(entry.currentPosition);
                }
            }
            mc.worldRenderer.reload();
        }
    }

    public static void onLightUpdateEvent(int chunkX, int chunkZ, CallbackInfo ci) {
          

        ListType listtype = (ListType) Configs.Lists.SELECTIVE_BLOCKS_LIST_TYPE.getOptionListValue();

        if (listtype == ListType.NONE) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean found = false;
        if (mc != null && mc.world != null && mc.world.getLightingProvider() != null) {

            ConcurrentHashMap<Long, ListMapEntry> list = (listtype == ListType.WHITELIST) ? SELECTIVE_WHITELIST
                    : SELECTIVE_BLACKLIST;

            int minX = chunkX * 16 - 1;
            int minZ = chunkZ * 16 - 1;
            int maxX = chunkX * 16 + 16;
            int maxZ = chunkZ * 16 + 16;

            for (ListMapEntry entry : list.values()) {
               
                int x = entry.currentPosition.getX();
                int z = entry.currentPosition.getZ();
                if (x >= minX && z >= minZ && x <= maxX && z <= maxZ) {
                    found = true;
                    break;
                }
            }
        }
        if (found) {
             ci.cancel();
        }
        
    }

    public static void rebuildStrings() {
        String whitelist = getStringFromMap(SELECTIVE_WHITELIST);
        String blacklist = getStringFromMap(SELECTIVE_BLACKLIST);

        Configs.Lists.SELECTIVE_BLOCKS_WHITELIST.setValueFromString(whitelist);
        Configs.Lists.SELECTIVE_BLOCKS_BLACKLIST.setValueFromString(blacklist);
    }

    public static void putMapFromString(ConcurrentHashMap<Long, ListMapEntry> map, String str) {

        String[] parts = str.split("\\|");

        for (int i = 0; i < parts.length; i++) {
            String[] nums = parts[i].split(",");
            if (nums.length < 3)
                continue;

            try {
                int x = Integer.parseInt(nums[0]);
                int y = Integer.parseInt(nums[1]);
                int z = Integer.parseInt(nums[2]);
                // System.out.println(x + "," + y + "," + z);
                BlockPos pos = new BlockPos(x, y, z);
                map.put(pos.asLong(), new ListMapEntry(pos, true));
            } catch (NumberFormatException e) {

                Tweakeroo.logger.warn("Error while parsing int: " + e.toString());
            }
        }
    }

    public static String getStringFromMap(ConcurrentHashMap<Long, ListMapEntry> map) {

        Iterator<ListMapEntry> iterator = map.values().iterator();

        ArrayList<String> entries = new ArrayList<String>();

        while (iterator.hasNext()) {
            ListMapEntry entry = iterator.next();
            entries.add(entry.currentPosition.getX() + "," + entry.currentPosition.getY() + ","
                    + entry.currentPosition.getZ());
        }
        return String.join("|", entries);
    }

    static class ListMapEntry {
        public BlockPos originalPosition;
        public BlockPos currentPosition;
        public boolean preserve = false;

        ListMapEntry(BlockPos pos) {
            originalPosition = pos;
            currentPosition = pos;
        }
        ListMapEntry(BlockPos pos, boolean preserve) {
            this(pos);
            this.preserve = preserve;
        }
    }

    static class Selection {
        public BlockPos pos1 = null;
        public BlockPos pos2 = null;
    }

}
