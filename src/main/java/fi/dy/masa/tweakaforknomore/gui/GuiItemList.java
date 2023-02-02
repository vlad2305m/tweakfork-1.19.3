package fi.dy.masa.tweakaforknomore.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import fi.dy.masa.malilib.data.DataDump;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakaforknomore.gui.widgets.WidgetItemListEntry;
import fi.dy.masa.tweakaforknomore.gui.widgets.WidgetListItemList;
import fi.dy.masa.tweakaforknomore.items.ItemList;
import fi.dy.masa.tweakaforknomore.items.ItemListEntry;
import fi.dy.masa.tweakaforknomore.items.ItemListSorter;
import fi.dy.masa.tweakeroo.Reference;

public class GuiItemList extends GuiListBase<ItemListEntry, WidgetItemListEntry, WidgetListItemList>
//     implements ISelectionListener<ItemListEntry>
{
    public static final GuiItemList INSTANCE = new GuiItemList(ItemList.INSTANCE);
    
    private final ItemList itemList;

    public GuiItemList(ItemList itemList)
    {
        super(10, 44);

        this.itemList = itemList;
        this.title = this.itemList.getTitle();
        this.useTitleHierarchy = false;

        WidgetItemListEntry.setMaxNameLength(itemList.getItemsAll());

    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 80;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.itemList.reCreateItemList();
        WidgetItemListEntry.setMaxNameLength(itemList.getItemsAll());
       

        int x = 12;
        int y = 24;

        
        int gap = 1;
      

        x += this.createButtonOnOff(x, y, -1, this.itemList.getScanToggle(), ButtonListener.Type.SCAN_TOGGLE) + gap;
        x += this.createButtonOnOff(x, y, -1, this.itemList.getCountsRenderToggle(), ButtonListener.Type.RENDER_COUNTS_TOGGLE) + gap;
        x += this.createButton(x, y, -1, ButtonListener.Type.CLEAR_SELECTED) + gap;
        x += this.createButton(x, y, -1, ButtonListener.Type.CLEAR_CACHE) + gap;
        x += this.createButton(x, y, -1, ButtonListener.Type.WRITE_TO_FILE) + gap;
        y += 22;

        y = this.height - 36;

        // Progress: Done xx % / Missing xx % / Wrong xx %
        long itemCount = this.itemList.getCountTotal();
        long itemTypesCount = this.itemList.getItemTypesCountTotal();

        long blocksScanned = this.itemList.getScanCount();
        long containerCount = this.itemList.getContainerCount();

      

        String str = String.format("%d Items, %d Types. Scanned %d / %d", itemCount, itemTypesCount, blocksScanned, containerCount);
        int w = this.getStringWidth(str);
        this.addLabel(12, this.height - 36, w, 12, 0xFFFFFFFF, str);
        
    }

    private int createButton(int x, int y, int width, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type, this);
        String label = type.getDisplayName();
        

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);

        if (type == ButtonListener.Type.WRITE_TO_FILE)
        {
            button.setHoverStrings("tweakaforknomore.gui.button.hover.item_list.write_hold_shift_for_csv");
        }

        this.addButton(button, listener);

        return button.getWidth();
    }

    private int createButtonOnOff(int x, int y, int width, boolean isCurrentlyOn, ButtonListener.Type type)
    {
        ButtonOnOff button = new ButtonOnOff(x, y, width, false, type.getTranslationKey(), isCurrentlyOn);
        this.addButton(button, new ButtonListener(type, this));
        return button.getWidth();
    }

    public ItemList getItemList()
    {
        return this.itemList;
    }


    @Override
    protected WidgetListItemList createListWidget(int listX, int listY)
    {
        return new WidgetListItemList(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this);
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiItemList parent;
        private final Type type;

        public ButtonListener(Type type, GuiItemList parent)
        {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            ItemList itemList = this.parent.itemList;

            switch (this.type)
            {

                case SCAN_TOGGLE:
                    itemList.toggleScan();
                    break;
                case RENDER_COUNTS_TOGGLE:
                    itemList.toggleCountsRender();
                    break;
                case CLEAR_SELECTED:
                    itemList.clearSelected();
                    break;
                case CLEAR_CACHE:
                    itemList.clearCache();
                    this.parent.addMessage(MessageType.SUCCESS, 3000, "tweakaforknomore.message.cache_cleared");
                    break;

                case WRITE_TO_FILE:
                    File dir = new File(FileUtils.getConfigDirectory(), Reference.MOD_ID);
                    boolean csv = GuiBase.isShiftDown();
                    String ext = csv ? ".csv" : ".txt";
                    File file = DataDump.dumpDataToFile(dir, "item_list", ext, this.getItemListDump(itemList, csv).getLines());

                    if (file != null)
                    {
                        String key = "tweakaforknomore.message.item_list_written_to_file";
                        this.parent.addMessage(MessageType.SUCCESS, key, file.getName());
                        StringUtils.sendOpenFileChatMessage(this.parent.mc.player, key, file);
                    }
                    break;
            }

            this.parent.initGui(); // Re-create buttons/text fields
        }

        private DataDump getItemListDump(ItemList itemList, boolean csv)
        {
            DataDump dump = new DataDump(4, csv ? DataDump.Format.CSV : DataDump.Format.ASCII);

            ArrayList<ItemListEntry> list = new ArrayList<>();
            list.addAll(itemList.getItemsFiltered(false));
            Collections.sort(list, new ItemListSorter(itemList));

            for (ItemListEntry entry : list)
            {
                int total = entry.getCountTotal();
                int boxes = entry.getCountBoxes();
                int containers = entry.getCountContainers();
                dump.addData(entry.getItemName(), String.valueOf(total), String.valueOf(boxes), String.valueOf(containers));
            }

            dump.addTitle("Item", "Total", "Boxes", "Containers");
            dump.addHeader(itemList.getTitle());
            dump.setColumnProperties(1, DataDump.Alignment.RIGHT, true); // total
            dump.setColumnProperties(2, DataDump.Alignment.RIGHT, true); // boxes
            dump.setColumnProperties(3, DataDump.Alignment.RIGHT, true); // containers
            dump.setSort(false);
            dump.setUseColumnSeparator(true);

            return dump;
        }

        public enum Type
        {
            CLEAR_SELECTED      ("tweakaforknomore.gui.button.item_list.clear_selected"),
            CLEAR_CACHE         ("tweakaforknomore.gui.button.item_list.clear_cache"),
            WRITE_TO_FILE       ("tweakaforknomore.gui.button.item_list.write_to_file"),
            SCAN_TOGGLE         ("tweakaforknomore.gui.button.item_list.scan_toggle"),
            RENDER_COUNTS_TOGGLE       ("tweakaforknomore.gui.button.item_list.render_counts_toggle");

            private final String translationKey;

            private Type(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getTranslationKey()
            {
                return this.translationKey;
            }

            public String getDisplayName(Object... args)
            {
                return StringUtils.translate(this.translationKey, args);
            }
        }
    }

    //@Override
    public void onSelectionChange(ItemListEntry entry) {
        if (entry == null) return;

        this.itemList.toggleEntrySelection(entry);
        
    }

    // @Override
    // protected ISelectionListener<ItemListEntry> getSelectionListener()
    // {
    //     return this;
    // }

}