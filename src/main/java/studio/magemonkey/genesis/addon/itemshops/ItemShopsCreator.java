package studio.magemonkey.genesis.addon.itemshops;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import studio.magemonkey.genesis.Genesis;
import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.GenesisShops;
import studio.magemonkey.genesis.core.prices.GenesisPriceType;
import studio.magemonkey.genesis.core.rewards.GenesisRewardType;
import studio.magemonkey.genesis.managers.ClassManager;

import java.util.List;

public class ItemShopsCreator {
    private final double priceMultiplier, rewardMultiplier;
    @Getter
    private final boolean advancedStyle, allowSell, allowBuy, allowBuyAll, allowSellAll, worthIsForOneUnit;
    private final String                   currency;
    private final ItemShopsCreatorSimple   creatorSimple;
    private final ItemShopsCreatorAdvanced creatorAdvanced;

    private GenesisPriceType  priceType;
    private GenesisRewardType rewardType;

    public ItemShopsCreator(FileConfiguration c) {
        priceMultiplier = c.getDouble("PriceMultiplier");
        rewardMultiplier = c.getDouble("RewardMultiplier");
        currency = c.getString("CurrencyType");
        advancedStyle = c.getBoolean("UseAdvancedStyle");
        allowSell = c.getBoolean("AllowSell");
        allowBuy = c.getBoolean("AllowBuy");
        allowSellAll = c.getBoolean("AllowSellAll");
        allowBuyAll = c.getBoolean("AllowBuyAll");
        worthIsForOneUnit = c.getBoolean("WorthIsForOneUnit");
        creatorSimple = new ItemShopsCreatorSimple(c);
        creatorAdvanced = new ItemShopsCreatorAdvanced(c);
    }

    public void loadItemShop(GenesisShops shopHandler, GenesisShop shop, List<ISItem> items, Genesis plugin) {
        if (priceType == null) {
            priceType = GenesisPriceType.detectType(currency);
            priceType.enableType();
            rewardType = GenesisRewardType.detectType(currency);
            rewardType.enableType();
        }

        for (ISItem item : items) {
            GenesisBuy buy = createBuyItem(shopHandler, shop, item, plugin);
            shop.addShopItem(buy, buy.getItem(), ClassManager.manager);
        }

    }

    private GenesisBuy createBuyItem(GenesisShops shopHandler, GenesisShop shop, ISItem item, Genesis plugin) {
        if (advancedStyle) {
            return creatorAdvanced.createBuyItem(shopHandler, shop, item, plugin, rewardType, priceType,
                    rewardMultiplier,
                    priceMultiplier, worthIsForOneUnit);
        } else {
            return creatorSimple.createBuyItem(shop, item, plugin, rewardType, priceType, rewardMultiplier,
                    priceMultiplier, worthIsForOneUnit);
        }
    }
}
