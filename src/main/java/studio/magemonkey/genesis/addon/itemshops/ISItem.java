package studio.magemonkey.genesis.addon.itemshops;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.genesis.core.rewards.GenesisRewardType;
import studio.magemonkey.genesis.core.rewards.GenesisRewardTypeNumber;
import studio.magemonkey.genesis.managers.ClassManager;

import java.util.ArrayList;
import java.util.List;

public class ISItem {
    @Getter
    private final String path;
    private final double worth, fixSell, fixBuy;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final boolean   allowSell;
    @Getter
    private final boolean   allowBuy;
    private final boolean   allowSellAll;
    private final boolean   allowBuyAll;

    @Getter
    private final List<String> itemData;

    public ISItem(String path,
                  double worth,
                  double fixBuy,
                  double fixSell,
                  List<String> itemData,
                  boolean allowSell,
                  boolean allowBuy,
                  boolean allowSellAll,
                  boolean allowBuyAll) {
        this.path = path;
        this.worth = worth;
        this.fixBuy = fixBuy;
        this.fixSell = fixSell;
        this.itemData = itemData;
        this.allowSell = allowSell;
        this.allowBuy = allowBuy;
        this.allowSellAll = allowSellAll;
        this.allowBuyAll = allowBuyAll;
        this.itemStack = ClassManager.manager.getItemStackCreator().createItemStack(itemData, false);
    }

    /*
     * Note: There are two kinds of amounts: 1. Server owners can give items in the config an amount 2. In advanced shops items get a certain amount
     * Advanced shops need to set "receive worth of one unit" to true and include an own amount, while simple shops set amount to 1 because they get the predefined amount.
     */
    public Number getWorth(boolean typeBuy,
                           double factor,
                           int amount,
                           GenesisRewardType type,
                           boolean receiveWorthOfOneUnit,
                           boolean worthIsForOneUnit) {
        double d = worth * factor;

        if (typeBuy && fixBuy > 0) { // Got fix values? Replace by them
            d = fixBuy;
        } else if (!typeBuy && fixSell > 0) {
            d = fixSell;
        }

        d *= amount;

        if (receiveWorthOfOneUnit & !worthIsForOneUnit) {
            d /= itemStack.getAmount(); // Can happen in advanced shop
        } else if (!receiveWorthOfOneUnit && worthIsForOneUnit) {
            d *= itemStack.getAmount(); // Can happen in simple shop
        }

        if (type instanceof GenesisRewardTypeNumber) {
            GenesisRewardTypeNumber n = (GenesisRewardTypeNumber) type;
            if (n.isIntegerValue()) {
                return (int) d;
            }
        }
        return d;
    }

    @Deprecated
    public double getWorth() {
        return worth;
    }

    public boolean isAllowSellAll() {
        return allowSellAll && allowSell;
    }

    public boolean isAllowBuyAll() {
        return allowBuyAll && allowBuy;
    }

    public List<ItemStack> getItemList(int amount) {
        List<ItemStack> list = new ArrayList<>();
        ItemStack       item = itemStack.clone();
        item.setAmount(amount);
        list.add(item);
        return list;
    }
}
