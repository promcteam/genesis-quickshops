package studio.magemonkey.genesis.addon.quickshops;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.genesis.managers.ClassManager;

import java.util.ArrayList;
import java.util.List;

public class ItemInfo {
    private final List<String> menuitem;
    private final String[]     messages;

    public ItemInfo(List<String> menuitem, String message) {
        this(menuitem, new String[]{message});
    }

    public ItemInfo(List<String> menuitem, String[] messages) {
        this.menuitem = menuitem;
        this.messages = messages;
    }


    public ItemInfo(ConfigurationSection section) {
        if (section != null) {
            this.menuitem = section.getStringList("MenuItem");
            this.messages = new String[]{section.getString("Message")};
        } else {
            this.menuitem = new ArrayList<String>();
            this.messages = new String[]{"message not found"};
        }
    }

    public ItemInfo(ConfigurationSection section, String[] messagePaths) {
        this.menuitem = section.getStringList("MenuItem");
        this.messages = new String[messagePaths.length];
        for (int i = 0; i < messagePaths.length; i++) {
            messages[i] = section.getString(messagePaths[i]);
        }
    }


    public ItemStack getMenuItem(List<String> itemData, ItemStack itemstack, int amount) {
        List<String> new_list = new ArrayList<>();
        if (itemData != null) {
            for (String entry : itemData) {
                new_list.add(transformEntry(entry, itemstack, amount));
            }
        }
        if (menuitem != null) {
            for (String entry : menuitem) {
                new_list.add(transformEntry(entry, itemstack, amount));
            }
        }
        return ClassManager.manager.getItemStackCreator().createItemStack(new_list, false);
    }

    @SuppressWarnings("deprecation")
    public String transformEntry(String entry, ItemStack itemstack, int amount) {
        entry = entry.replace("%amount%", String.valueOf(amount));
        if (itemstack != null) {
            entry = entry.replace("%type%", ClassManager.manager.getItemStackTranslator().readMaterial(itemstack));
            entry = entry.replace("%durability%", String.valueOf(itemstack.getDurability()));
        }
        return entry;
    }

    public String getMessage() {
        return getMessage(0);
    }

    public String getMessage(int id) {
        return messages[id];
    }
}
