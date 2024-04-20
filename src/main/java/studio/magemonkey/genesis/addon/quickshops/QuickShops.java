package studio.magemonkey.genesis.addon.quickshops;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import studio.magemonkey.genesis.api.GenesisAddonConfigurable;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.GenesisShops;
import studio.magemonkey.genesis.managers.config.FileHandler;

import java.util.List;

public class QuickShops extends GenesisAddonConfigurable {
    private QuickShopsCreator creator;

    @Override
    public String getAddonName() {
        return "QuickShops";
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
            new FileHandler().copyFromJar(this, getGenesis(), true, "QuickShopExample.yml", "QuickShopExample.yml");
        } else {
            new FileHandler().copyDefaultsFromJar(this, "config.yml");
        }

        reloadConfig();
        FileConfiguration c = getConfig();
        creator = new QuickShopsCreator(c);

    }

    @Override
    public void disableAddon() {
    }

    public QuickShopsCreator getCreator() {
        return creator;
    }

    @Override
    public void genesisReloaded(CommandSender sender) {
        load();
    }

    @Override
    public void genesisFinishedLoading() {}

    public void loadQuickShop(GenesisShops shopHandler, GenesisShop shop, List<ISItem> items) {
        creator.loadQuickShop(shopHandler, shop, items, getGenesis());
    }

    @Override
    public boolean saveConfigOnDisable() {
        return false;
    }
}
