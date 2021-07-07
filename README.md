## Tweakfork
Tweakfork is a fork of Masa/Maruhon's mod [Tweakeroo](https://github.com/maruohon/tweakeroo). Its basically the same thing, except with some small changes to make life a little bit easier. 

1. Multi-layer block break restriction -> can prevent you from making potholes in the ground when speedmining
2. Block break whitelist/blacklist restriction -> can prevent you from accidentally mining the wrong block and flooding your redstone
3. Block based right click whitelist/blacklist restriction -> can prevent you from accidentally placing water outside of cauldrons and flooding your redstone
4. Toggleable flexible block placement -> may save you from arthritis
5. Stacking flexible block placement -> apply rotation to adjacent/offset placements
6. Piston info tweak to show piston update order & counts -> Easier slimestone. Improving on the [PistOrder mod](https://github.com/Fallen-Breath/pistorder) by Fallen Breath. (you can now have multiple toggled & have hotkeys)
7. tweakHideScoreboard -> may save your sanity when servers turn on sidebar scoreboards during big digs
8. disableClientBlockEvents -> stops client side piston lag
9. Fixed afterClicker with accurate placement protocol -> so placing repeaters isn't pain
10. tweakDayCycleOverride -> Overrides the time of day client-side
11. tweakNoSneakSlowdown -> Doesnt slow you down when you sneak
12. tweakScaffoldPlace -> Place blocks in a similar way to how scaffold placing works.
13. disableNametags -> Disables nametag rendering for unobstrusive replays
14. disableBossBar -> Disables the boss bar rendering
15. chestMirrorFix -> Fixes wonky chest mirroring behavior (works with litematica).
16. tweakContainerScan -> Scans containers and tells you how many items/types are in it. Useful for debugging storage tech.
    * Easily see which containers have items or not
    * Compile a list of all the items it found
    * Search the list and click to easily find the location of the items.
17. Selectively Visible Blocks -> Allows you to make nice replays/vids with transparent parts (works with Optifucked). Selectively renders blocks depending on position based white/blacklists.
    * Lists.selectiveBlocksWhitelist and Lists.selectiveBlocksBlacklist -> strings encoding the white/blacklists. Format: `x,y,z|x2,y2,z2|...`
    * tweakAreaSelector -> allows you to add/remove selection from list easily with areaSelectionAddToList and areaSelectionRemoveFromList hotkeys. works in replay editor. Use areaSelectionOffset hotkey to offset selection box.
    * tweakSelectiveBlocksRendering -> Turns on selective block rendering with the lists. Turn on before you render replay.
    * tweakSelectiveBlocksRenderOutline -> Renders an outline over the listed positions. Allows you to see whats in the white/blacklists.
    * Generic.selectiveBlocksTrackPistons -> When enabled, the mod will track piston movements and update the position lists accordingly.
    * Generic.areaSelectionUseAll -> When enabled, the area selector will include air blocks.
    * Generic.selectiveBlocksNoHit -> When enabled, disables trace-hitting hidden blocks. (hint: can also use to build stuff in hard to reach places in survival mode)
    * Generic.selectiveBlocksHideEntities -> When enabled, selective block rendering will also hide entities.
    * Generic.selectiveBlocksHideParticles -> When enabled, selective block rendering will also hide particles.

*Using Selectively Visible Blocks for the [brewer](https://www.youtube.com/watch?v=1_jSkyq-WOs) video*
<img width="1280" alt="Screen Shot 2021-02-16 at 2 01 08 PM" src="https://user-images.githubusercontent.com/13282284/108109105-7742c280-705f-11eb-81cf-b5341ca740c3.png">

Tweakeroo
==============
Tweakeroo is a client-side-only Minecraft mod using LiteLoader.
It adds a selection of miscellaneous, configurable, client-side tweaks to the game.
Some examples of these are the "flexible block placement" tweak and the "fast block placement" tweak.
For more information and the downloads (compiled builds), see http://minecraft.curseforge.com/projects/tweakeroo

Compiling
=========
* Clone the repository
* Open a command prompt/terminal to the repository directory
* run 'gradlew build'
* The built jar file will be in build/libs/
