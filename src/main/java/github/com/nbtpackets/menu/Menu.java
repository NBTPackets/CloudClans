package github.com.nbtpackets.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface Menu {
    void open(Player player);
    void handleClick(Player player, InventoryClickEvent event);
    String getId();
}