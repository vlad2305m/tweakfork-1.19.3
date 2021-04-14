package fi.dy.masa.tweakeroo.items;

import java.util.HashMap;
import java.util.HashSet;

import fi.dy.masa.malilib.util.ItemType;
import fi.dy.masa.tweakeroo.tweaks.RenderTweaks.ContainerEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemListEntry
{
    private final ItemType item;
    public int countTotal;
    public int countBoxes;
    public int countItemsInBoxes;
    public HashSet<ContainerEntry> containers = new HashSet<ContainerEntry>();

    public ItemListEntry(ItemStack stack, int countTotal, int countBoxes, int countContainer, int countItemsInBoxes)
    {
        this.item = new ItemType(stack, false, false);
        this.countTotal = countTotal;
        this.countBoxes = countBoxes;
        this.countItemsInBoxes = countItemsInBoxes;
    }

    public String getItemName() {
        return this.getItem().getName().getString();
    }
    public ItemStack getStack()
    {
        return this.item.getStack();
    }
    public Item getItem() {
        return this.getStack().getItem();
    }

    /**
     * Returns the total number of required items of this type in the counted area.
     * @return
     */
    public int getCountTotal()
    {
        return this.countTotal;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.item == null) ? 0 : this.item.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemListEntry other = (ItemListEntry) obj;
        if (this.item == null)
        {
            if (other.item != null)
                return false;
        }
        else if (! this.item.equals(other.item))
            return false;
        return true;
    }

    public int getCountContainers() {
        return this.containers.size();
    }

    public int getCountBoxes() {
        return this.countBoxes;
    }

    public int getCountItemsInBoxes() {
        return this.countItemsInBoxes;
    }
}