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
10. Selectively Visible Blocks -> Allows you to make nice replays/vids with transparent parts

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
