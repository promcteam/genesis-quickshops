package studio.magemonkey.genesis.addon.quickshops;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.events.GenesisLoadShopItemsEvent;
import studio.magemonkey.genesis.managers.config.GenesisConfigShop;

import java.util.ArrayList;
import java.util.List;

public class GenesisListener implements Listener {
    private final QuickShops shops;

    public GenesisListener(QuickShops shops) {
        this.shops = shops;
    }

    @EventHandler
    public void loadShop(GenesisLoadShopItemsEvent event) {
        GenesisShop shop = event.getShop();
        if (shop instanceof GenesisConfigShop) {
            GenesisConfigShop    c = (GenesisConfigShop) shop;
            ConfigurationSection s = c.getConfig();
            if (s.contains("quickshop")) {
                s = s.getConfigurationSection("quickshop");

                List<ISItem> items = new ArrayList<ISItem>();

                for (String key : s.getKeys(false)) {
                    ISItem item = loadItem(s.getConfigurationSection(key),
                            shops.getCreator().isAllowSell(),
                            shops.getCreator().isAllowBuy(),
                            shops.getCreator().isAllowSellAll(),
                            shops.getCreator().isAllowBuyAll());
                    if (item != null) {
                        items.add(item);
                    }
                }

                if (!items.isEmpty()) {
                    shops.loadQuickShop(event.getShopHandler(), shop, items);
                }

            }

        }
    }

    private ISItem loadItem(ConfigurationSection section,
                            boolean allowSell,
                            boolean allowBuy,
                            boolean allowSellAll,
                            boolean allowBuyAll) {
        if (section != null) {
            double       worth    = section.getDouble("Worth");
            double       fixBuy  = section.getDouble("PriceBuy", -1);
            double       fixSell = section.getDouble("RewardSell", -1);
            List<String> itemData = section.getStringList("Item");

            if (section.contains("AllowSell")) {
                allowSell = section.getBoolean("AllowSell");
            }
            if (section.contains("AllowBuy")) {
                allowBuy = section.getBoolean("AllowBuy");
            }
            if (section.contains("AllowSellAll")) {
                allowSellAll = section.getBoolean("AllowSellAll");
            }
            if (section.contains("AllowBuyAll")) {
                allowBuyAll = section.getBoolean("AllowBuyAll");
            }

            return new ISItem(section.getName(),
                    worth,
                    fixBuy,
                    fixSell,
                    itemData,
                    allowSell,
                    allowBuy,
                    allowSellAll,
                    allowBuyAll);
        }
        return null;
    }
}
