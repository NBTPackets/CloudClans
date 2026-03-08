package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.util.Lang;

import java.util.Map;

public class DeleteClan implements MenuAction {
    private final String clanName;

    public DeleteClan(String clanName) {
        this.clanName = clanName;
    }

    @Override
    public void execute(Player player) {
        CloudClans.getInstance().getClanManager().deleteClan(clanName);
        Lang.send(player, "delete.deleted", Map.of("clan", clanName));
        player.closeInventory();
    }
}