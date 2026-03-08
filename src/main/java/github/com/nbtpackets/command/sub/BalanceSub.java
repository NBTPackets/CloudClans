package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.economy.EconomyManager;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.model.Clan;
import github.com.nbtpackets.util.Lang;

import java.util.Map;

@RequiredArgsConstructor
public class BalanceSub implements SubCommand {

    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId()).ifPresentOrElse(
                clan -> sendBalanceInfo(player, clan),
                () -> Lang.send(player, "balance.no-clan")
        );
    }

    private void sendBalanceInfo(Player player, Clan clan) {
        EconomyManager economy = CloudClans.getInstance().getEconomyManager();
        String formattedBalance = economy.format(clan.balance());

        Lang.send(player, "balance.info", Map.of(
                "clan", clan.name(),
                "balance", formattedBalance
        ));
    }

    @Override
    public String getName() {return "balance";}
}