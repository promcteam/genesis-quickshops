package studio.magemonkey.genesis.addon.itemshops;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.genesis.Genesis;
import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.GenesisShops;
import studio.magemonkey.genesis.core.prices.GenesisPriceType;
import studio.magemonkey.genesis.core.rewards.GenesisRewardType;
import studio.magemonkey.genesis.managers.ClassManager;

public class ItemShopsCreatorAdvanced {
    private final String look_adv_subshop_displayname;

    private final ItemInfo preview, buy, sell, sellAll, buyAll, back, close;


    public ItemShopsCreatorAdvanced(FileConfiguration c) {
        ConfigurationSection shopAdvancedSubShop = c.getConfigurationSection("ShopItemLookAdvanced.SubShop");
        look_adv_subshop_displayname = shopAdvancedSubShop.getString("Displayname");

        preview = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.Preview"));
        buy = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.Buy"));
        sell = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.Sell"));
        sellAll = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.SellAll"));
        buyAll = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.BuyAll"));
        back = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.Back"));
        close = new ItemInfo(c.getConfigurationSection("ShopItemLookAdvanced.Close"));
    }

    public GenesisBuy createBuyItem(GenesisShops shopHandler,
                                    GenesisShop shop,
                                    ISItem item,
                                    Genesis plugin,
                                    GenesisRewardType rewardType,
                                    GenesisPriceType priceType,
                                    double rewardMultiplier,
                                    double priceMultiplier,
                                    boolean worthIsForOneUnit) {
        String shopName = "itemshop_advanced_" + item.getPath().toLowerCase();
        GenesisBuy buy = new GenesisBuy(GenesisRewardType.Shop,
                GenesisPriceType.Nothing,
                shopName,
                null,
                preview.getMessage(),
                -1,
                null,
                item.getPath());

        buy.setItem(preview.getMenuItem(item.getItemData(), item.getItemStack(), 1), false);

        shopHandler.addShop(createSubShop(shopHandler,
                shop,
                buy,
                item,
                shopName,
                plugin,
                rewardType,
                priceType,
                rewardMultiplier,
                priceMultiplier,
                worthIsForOneUnit));
        return buy;
    }

    public GenesisShop createSubShop(GenesisShops shopHandler,
                                     GenesisShop shop,
                                     GenesisBuy buy,
                                     ISItem item,
                                     String shopName,
                                     Genesis plugin,
                                     GenesisRewardType rewardType,
                                     GenesisPriceType priceType,
                                     double rewardMultiplier,
                                     double priceMultiplier,
                                     boolean worthIsForOneUnit) {
        GenesisShop individualShop = new GenesisShop(shopHandler.createId(),
                shopName,
                null,
                true,
                plugin,
                ClassManager.manager.getStringManager()
                        .transform(preview.transformEntry(look_adv_subshop_displayname.replace("%parentshopname%",
                                        shop.getValidDisplayName(null, null)),
                                item.getItemStack(),
                                item.getItemStack().getAmount()), buy, shop, null, null),
                0,
                null) {

            @Override
            public void reloadShop() {
            }
        };

        int[] levels = null;

        switch (item.getItemStack().getMaxStackSize()) {
            case 1:
                levels = new int[]{1};
                break;
            case 8:
                levels = new int[]{1, 8};
                break;
            case 16:
                levels = new int[]{1, 4, 16};
                break;
            case 32:
                levels = new int[]{1, 8, 32};
                break;
            case 64:
                levels = new int[]{1, 8, 64};
                break;
        }

        if (levels != null) {
            for (int level = 0; level < levels.length; level++) {
                int amount     = levels[level];
                int first_slot = level * 9 + 3;

                addBuyItemPreview(individualShop, item, first_slot + 1, amount);

                if (item.isAllowBuy()) {
                    addBuyItemBuy(individualShop,
                            priceType,
                            rewardType,
                            item,
                            priceMultiplier,
                            first_slot,
                            amount,
                            worthIsForOneUnit);
                } else if (item.isAllowSell()) {
                    addBuyItemSell(individualShop,
                            rewardType,
                            item,
                            rewardMultiplier,
                            first_slot,
                            amount,
                            worthIsForOneUnit);
                }

                if (item.isAllowSell()) {
                    addBuyItemSell(individualShop,
                            rewardType,
                            item,
                            rewardMultiplier,
                            first_slot + 2,
                            amount,
                            worthIsForOneUnit);
                } else if (item.isAllowBuy()) {
                    addBuyItemBuy(individualShop,
                            priceType,
                            rewardType,
                            item,
                            priceMultiplier,
                            first_slot + 2,
                            amount,
                            worthIsForOneUnit);
                }
            }
        }


        if (item.isAllowBuyAll() && item.isAllowSellAll()) {
            addBuyItemBuyAll(individualShop,
                    priceType,
                    rewardType,
                    item,
                    priceMultiplier,
                    (levels.length) * 9 + 3,
                    worthIsForOneUnit);
            addBuyItemSellAll(individualShop,
                    rewardType,
                    item,
                    rewardMultiplier,
                    (levels.length) * 9 + 5,
                    worthIsForOneUnit);
        } else {

            if (item.isAllowSellAll()) {
                addBuyItemSellAll(individualShop,
                        rewardType,
                        item,
                        rewardMultiplier,
                        (levels.length) * 9 + 4,
                        worthIsForOneUnit);
            } else if (item.isAllowBuyAll()) {
                addBuyItemBuyAll(individualShop,
                        priceType,
                        rewardType,
                        item,
                        priceMultiplier,
                        (levels.length) * 9 + 4,
                        worthIsForOneUnit);
            }

        }

        addBuyItemBack(individualShop, shop, (levels.length + 1) * 9);
        addBuyItemClose(individualShop, (levels.length + 2) * 9 - 1);

        individualShop.finishedAddingItems();

        return individualShop;
    }

    private GenesisBuy addBuyItemBuy(GenesisShop individualShop,
                                     GenesisPriceType priceType,
                                     GenesisRewardType rewardType,
                                     ISItem item,
                                     double priceMultiplier,
                                     int slot,
                                     int amount,
                                     boolean worthIsForOneUnit) {
        GenesisBuy shopItem = new GenesisBuy(GenesisRewardType.Item,
                priceType,
                item.getItemList(amount),
                item.getWorth(true, priceMultiplier, amount, rewardType, true, worthIsForOneUnit),
                buy.getMessage(),
                slot,
                null,
                item.getPath() + "-buy-" + amount);
        shopItem.setShop(individualShop);
        individualShop.addShopItem(shopItem, buy.getMenuItem(null, null, amount), ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemBuyAll(GenesisShop individual_shop,
                                        GenesisPriceType priceType,
                                        GenesisRewardType rewardType,
                                        ISItem item,
                                        double priceMultiplier,
                                        int slot,
                                        boolean worthIsForOneUnit) {
        ItemStack i = item.getItemStack().clone();
        i.setAmount(1);
        GenesisBuy shopItem = new GenesisBuy(GenesisRewardType.ItemAll,
                priceType,
                i,
                item.getWorth(true, priceMultiplier, 1, rewardType, true, worthIsForOneUnit),
                buyAll.getMessage(),
                slot,
                null,
                item.getPath() + "-buyall");
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem, buyAll.getMenuItem(null, null, 1), ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemSell(GenesisShop individual_shop,
                                      GenesisRewardType rewardType,
                                      ISItem item,
                                      double rewardMultiplier,
                                      int slot,
                                      int amount,
                                      boolean worthIsForOneUnit) {
        GenesisBuy shopItem = new GenesisBuy(rewardType,
                GenesisPriceType.Item,
                item.getWorth(false, rewardMultiplier, amount, rewardType, true, worthIsForOneUnit),
                item.getItemList(amount),
                sell.getMessage(),
                slot,
                null,
                item.getPath() + "-sell-" + amount);
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem, sell.getMenuItem(null, null, amount), ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemSellAll(GenesisShop individual_shop,
                                         GenesisRewardType rewardType,
                                         ISItem item,
                                         double rewardMultiplier,
                                         int slot,
                                         boolean worthIsForOneUnit) {
        ItemStack i = item.getItemStack().clone();
        i.setAmount(1);
        GenesisBuy shopItem = new GenesisBuy(rewardType,
                GenesisPriceType.ItemAll,
                item.getWorth(false, rewardMultiplier, 1, rewardType, true, worthIsForOneUnit),
                i,
                sellAll.getMessage(),
                slot,
                null,
                item.getPath() + "-sellall");
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem, sellAll.getMenuItem(null, null, 1), ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemPreview(GenesisShop individual_shop, ISItem item, int slot, int amount) {
        GenesisBuy shopItem = new GenesisBuy(GenesisRewardType.Nothing,
                GenesisPriceType.Nothing,
                null,
                null,
                preview.getMessage(),
                slot,
                null,
                item.getPath() + "-preview-" + amount);
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem,
                preview.getMenuItem(item.getItemData(), item.getItemStack(), amount),
                ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemClose(GenesisShop individual_shop, int slot) {
        GenesisBuy shopItem = new GenesisBuy(GenesisRewardType.Close,
                GenesisPriceType.Nothing,
                null,
                null,
                close.getMessage(),
                slot,
                null,
                "close");
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem, close.getMenuItem(null, null, 1), ClassManager.manager);
        return shopItem;
    }

    private GenesisBuy addBuyItemBack(GenesisShop individual_shop, GenesisShop main_shop, int slot) {
        GenesisBuy shopItem = new GenesisBuy(GenesisRewardType.Shop,
                GenesisPriceType.Nothing,
                main_shop.getShopName().toLowerCase(),
                null,
                back.getMessage(),
                slot,
                null,
                "back");
        shopItem.setShop(individual_shop);
        individual_shop.addShopItem(shopItem, back.getMenuItem(null, null, 1), ClassManager.manager);
        return shopItem;
    }

}
