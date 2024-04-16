package studio.magemonkey.genesis.addon.itemshops;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import studio.magemonkey.genesis.api.GenesisAddonConfigurable;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.GenesisShops;
import studio.magemonkey.genesis.managers.config.FileHandler;

import java.util.List;

public class ItemShops extends GenesisAddonConfigurable {
    private ItemShopsCreator creator;

    @Override
    public String getAddonName() {
        return "ItemShops";
    }

    @Override
    public String getRequiredGenesisVersion() {
        return "1.0.0";
    }

    @Override
    public void enableAddon() {
        load();
        getServer().getPluginManager().registerEvents(new GenesisListener(this), this);
    }

    public void load() {
        if (!getAddonConfig().getFile().exists()) {
            new FileHandler().copyFromJar(this, "config.yml");

            new FileHandler().exportShops(getGenesis()); //Because shops are only imported when there is no shop folder already
            new FileHandler().copyFromJar(this, getGenesis(), true, "ItemShopExample.yml", "ItemShopExample.yml");
        } else {
            new FileHandler().copyDefaultsFromJar(this, "config.yml");
        }

        reloadConfig();
        FileConfiguration c = getConfig();
        creator = new ItemShopsCreator(c);

    }

    @Override
    public void disableAddon() {
    }

    public ItemShopsCreator getCreator() {
        return creator;
    }

    @Override
    public void genesisReloaded(CommandSender sender) {
        load();
    }

    @Override
    public void genesisFinishedLoading() {}

    public void loadItemShop(GenesisShops shopHandler, GenesisShop shop, List<ISItem> items) {
        creator.loadItemShop(shopHandler, shop, items, getGenesis());
    }

    @Override
    public boolean saveConfigOnDisable() {
        return false;
    }
}
