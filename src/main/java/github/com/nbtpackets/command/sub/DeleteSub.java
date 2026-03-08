package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.menu.impl.ConfirmDelete;
import github.com.nbtpackets.util.Lang;

@RequiredArgsConstructor
public class DeleteSub implements SubCommand {
    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId()).ifPresentOrElse(clan -> {
            if (!clan.isOwner(player.getUniqueId())) {
                Lang.send(player, "delete.not-owner");
                return;
            }

            MenuManager.openMenu(player, new ConfirmDelete(clan.name()));

        }, () -> Lang.send(player, "help.no-clan"));
    }

    @Override
    public String getName() {return "delete";}
}