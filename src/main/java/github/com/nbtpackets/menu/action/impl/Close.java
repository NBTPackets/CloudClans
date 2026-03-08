package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.menu.action.MenuAction;

public class Close implements MenuAction {
    @Override
    public void execute(Player player) {
        player.closeInventory();
        MenuManager.closeMenu(player);
    }
}