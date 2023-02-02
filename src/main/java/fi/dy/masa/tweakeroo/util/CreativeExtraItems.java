package fi.dy.masa.tweakeroo.util;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.mojang.brigadier.StringReader;

import javax.annotation.Nullable;

import fi.dy.masa.tweakeroo.Tweakeroo;
import net.minecraft.block.InfestedBlock;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.tweakeroo.Tweakeroo;

public class CreativeExtraItems
{
    private static final ArrayListMultimap<ItemGroup, ItemStack> ADDED_ITEMS = ArrayListMultimap.create();
    private static final HashMap<Item, ItemGroup> OVERRIDDEN_GROUPS = new HashMap<>();

    @Nullable
    public static ItemGroup getGroupFor(Item item)
    {
        return OVERRIDDEN_GROUPS.get(item);
    }

    public static List<ItemStack> getExtraStacksForGroup(ItemGroup group)
    {
        return ADDED_ITEMS.get(group);
    }

    public static void setCreativeExtraItems(List<String> items)
    {
        // The references are private without Fabric API module fabric-item-group-api-v1
        // So use an ugly workaround for now to find the correct group, to avoid the API
        // dependency and an extra Mixin accessor.
        // TODO 1.19.3+ ?
        for (ItemGroup group : ItemGroups.getGroups())
        {
            TextContent content = group.getDisplayName().getContent();

            if (content instanceof TranslatableTextContent translatableTextContent &&
                translatableTextContent.getKey().equals("itemGroup.op"))
            {
                setCreativeExtraItems(group, items);
                break;
            }
        }
    }

    private static void setCreativeExtraItems(ItemGroup group, List<String> items)
    {
        ADDED_ITEMS.clear();
        OVERRIDDEN_GROUPS.clear();

        if (items.isEmpty())
        {
            return;
        }

        Tweakeroo.logger.info("Adding extra items to creative inventory group '{}'", group.getDisplayName().getString());

        for (String str : items)
        {
            ItemStack stack = parseItemFromString(str);

            if (stack.isEmpty() == false)
            {
                if (stack.hasNbt())
                {
                    ADDED_ITEMS.put(group, stack);
                }
                else
                {
                    OVERRIDDEN_GROUPS.put(stack.getItem(), group);
                }
            }
        }
    }

    public static ItemStack parseItemFromString(String str)
    {
        try
        {
            ItemStringReader.ItemResult itemResult = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), new StringReader(str));
            Item item = itemResult.item().value();

            if (item != null)
            {
                ItemStack stack = new ItemStack(item);
                stack.setNbt(itemResult.nbt());
                return stack;
            }
        }
        catch (Exception e)
        {
            Tweakeroo.logger.warn("Invalid item '{}'", str);
        }

        return ItemStack.EMPTY;
    }

    public static void removeInfestedBlocks(DefaultedList<ItemStack> stacks)
    {
        stacks.removeIf((stack) -> stack.getItem() instanceof BlockItem &&
                                   ((BlockItem) stack.getItem()).getBlock() instanceof InfestedBlock);
    }
}
