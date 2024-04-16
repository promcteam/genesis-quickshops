package studio.magemonkey.genesis.addon.itemshops;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.genesis.Genesis;
import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.prices.GenesisPriceType;
import studio.magemonkey.genesis.core.rewards.GenesisRewardType;
import studio.magemonkey.genesis.inbuiltaddons.advancedshops.ActionSet;
import studio.magemonkey.genesis.inbuiltaddons.advancedshops.GenesisBuyAdvanced;

import java.util.HashMap;
import java.util.Map;

public class ItemShopsCreatorSimple {
    private final ItemInfo simple;

    public ItemShopsCreatorSimple(FileConfiguration c) {
        ConfigurationSection s_simple = c.getConfigurationSection("ShopItemLookSimple");
        simple = new ItemInfo(s_simple, new String[]{"MessageBuy", "MessageSell", "MessageSellAll"});
    }

    public GenesisBuy createBuyItem(GenesisShop shop,
                                    ISItem item,
                                    Genesis plugin,
                                    GenesisRewardType rewardType,
                                    GenesisPriceType priceType,
                                    double rewardMultiplier,
                                    double priceMultiplier,
                                    boolean worthIsForOneUnit) {
        Object buyReward = item.getItemList(item.getItemStack().getAmount());
        Object buyPrice  = item.getWorth(true, priceMultiplier, 1, rewardType, false, worthIsForOneUnit);

        Object sellPrice  = item.getItemList(item.getItemStack().getAmount());
        Object sellReward = item.getWorth(false, rewardMultiplier, 1, rewardType, false, worthIsForOneUnit);

        Object    sellAllReward = item.getWorth(false, rewardMultiplier, 1, rewardType, true, worthIsForOneUnit);
        ItemStack sellAllItem   = item.getItemStack().clone();
        sellAllItem.setAmount(1);


        Map<ClickType, ActionSet> actions = new HashMap<>();

        actions.put(ClickType.RIGHT,
                new ActionSet(rewardType,
                        GenesisPriceType.Item,
                        sellReward,
                        sellPrice,
                        simple.getMessage(1),
                        null,
                        null,
                        null));

        actions.put(ClickType.MIDDLE,
                new ActionSet(rewardType,
                        GenesisPriceType.ItemAll,
                        sellAllReward,
                        sellAllItem,
                        simple.getMessage(2),
                        null,
                        null,
                        null));

        GenesisBuy buy = new GenesisBuyAdvanced(GenesisRewardType.Item,
                priceType,
                buyReward,
                buyPrice,
                simple.getMessage(0),
                -1,
                null,
                item.getPath(),
                null,
                null,
                null,
                actions);

        buy.setItem(simple.getMenuItem(item.getItemData(), item.getItemStack(), item.getItemStack().getAmount()),
                false);

        return buy;
    }

}
