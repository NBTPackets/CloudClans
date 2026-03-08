package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerFind;

import java.util.Map;

@RequiredArgsConstructor
public class LeaveSub implements SubCommand {
    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId()).ifPresentOrElse(clan -> {
            if (clan.isOwner(player.getUniqueId())) {
                Lang.send(player, "leave.is-owner");
                return;
            }

            clanManager.removeMember(clan.name(), player.getUniqueId());
            Lang.send(player, "leave.success", Map.of("clan", clan.name()));

            PlayerFind.uuid(clan.owner()).ifPresent(owner ->
                    Lang.send(owner, "leave.left-notify", Map.of("player", player.getName()))
            );
        }, () -> Lang.send(player, "help.no-clan"));
    }

    @Override public String getName() { return "leave"; }
}