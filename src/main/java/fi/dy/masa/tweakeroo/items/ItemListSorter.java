package fi.dy.masa.tweakeroo.items;

import java.util.Comparator;

import fi.dy.masa.tweakeroo.items.ItemList.SortCriteria;

public class ItemListSorter implements Comparator<ItemListEntry>
{
    private final ItemList materialList;

    public ItemListSorter(ItemList materialList)
    {
        this.materialList = materialList;
    }

    @Override
    public int compare(ItemListEntry entry1, ItemListEntry entry2)
    {
        boolean reverse = this.materialList.getSortInReverse();
        SortCriteria sortCriteria = this.materialList.getSortCriteria();
        int nameCompare = entry1.getItem().getName().getString().compareTo(entry2.getItem().getName().getString());

        if (sortCriteria == SortCriteria.COUNT_TOTAL)
        {
            return entry1.getCountTotal() == entry2.getCountTotal() ? nameCompare : ((entry1.getCountTotal() > entry2.getCountTotal()) != reverse ? -1 : 1);
        }
        else if (sortCriteria == SortCriteria.COUNT_BOXES)
        {
            return entry1.getCountBoxes() == entry2.getCountBoxes() ? nameCompare : ((entry1.getCountBoxes() > entry2.getCountBoxes()) != reverse ? -1 : 1);
        }
        else if (sortCriteria == SortCriteria.COUNT_CONTAINERS)
        {
            return entry1.getCountContainers() == entry2.getCountContainers() ? nameCompare : ((entry1.getCountContainers() > entry2.getCountContainers()) != reverse ? -1 : 1);
        }

        return reverse == false ? nameCompare * -1 : nameCompare;
    }
}