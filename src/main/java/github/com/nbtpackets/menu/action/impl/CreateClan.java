package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.economy.EconomyManager;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.util.Lang;

import java.util.Map;

public class CreateClan implements MenuAction {
    private final String clanName;
    public CreateClan(String clanName) {
        this.clanName = clanName;
    }

    @Override
    public void execute(Player player) {
        EconomyManager economy = CloudClans.getInstance().getEconomyManager();
        double cost = CloudClans.getInstance().getConfig().getDouble("settings.create-cost", 0.0);
        if (cost > 0 && !economy.has(player, cost)) {
            Lang.send(player, "create.no-money", Map.of(
                    "cost", economy.format(cost),
                    "balance", economy.format(economy.getBalance(player))
            ));
            player.closeInventory();
            return;
        }

        if (cost > 0) {
            economy.withdraw(player, cost);
        }

        CloudClans.getInstance().getClanManager().createClan(clanName, player.getUniqueId());
        Lang.send(player, "create.created", Map.of("clan", clanName));
        player.closeInventory();
    }
}