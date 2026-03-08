package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.menu.impl.PlayerPerm;
import github.com.nbtpackets.perm.ClanPermRegistry;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.util.scheduler.Task;

import java.util.UUID;

public class TogglePerm implements MenuAction {

    private final UUID targetUuid;
    private final String permKey;

    public TogglePerm(UUID targetUuid, String permKey) {
        this.targetUuid = targetUuid;
        this.permKey = permKey.toUpperCase();
    }

    @Override
    public void execute(Player player) {
        if (!ClanPermRegistry.isRegistered(permKey)) {
            Lang.send(player, "perm.unknown-perm");
            return;
        }

        ClanManager cm = CloudClans.getInstance().getClanManager();

        cm.getPlayerClan(player.getUniqueId()).ifPresentOrElse(clan -> {
            if (!clan.isOwner(player.getUniqueId())) {
                Lang.send(player, "perm.not-leader");
                return;
            }
            if (!clan.members().contains(targetUuid)) {
                Lang.send(player, "perm.target-not-in-clan");
                return;
            }
            if (clan.isOwner(targetUuid)) {
                Lang.send(player, "perm.cannot-change-leader");
                return;
            }

            cm.toggleMemberPerm(clan.name(), targetUuid, permKey).ifPresent(granted ->
                    Task.sync(() ->
                            cm.getPlayerClan(player.getUniqueId()).ifPresent(updatedClan ->
                                    MenuManager.openMenu(player, new PlayerPerm(updatedClan, targetUuid))
                            )
                    )
            );
        }, () -> Lang.send(player, "perm.no-clan"));
    }
}