package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.glow.manager.GlowManager;
import github.com.nbtpackets.glow.manager.GlowUpdater;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.model.Clan;
import github.com.nbtpackets.perm.ClanPerm;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerFind;

import java.util.Map;
import java.util.Optional;

public class DisableGlow implements MenuAction {

    @Override
    public void execute(Player player) {
        ClanManager clanManager = CloudClans.getInstance().getClanManager();
        clanManager.getPlayerClan(player.getUniqueId())
                .ifPresentOrElse(
                        clan -> processDisableGlow(player, clan),
                        () -> Lang.send(player, "glow.no-clan")
                );
    }

    private void processDisableGlow(Player player, Clan clan) {
        if (!clan.hasPerm(player.getUniqueId(), ClanPerm.MANAGE_GLOW)) {
            Lang.send(player, "perm.no-perm");
            return;
        }
        Map<String, String> placeholders = Map.of("clan", clan.name());
        clan.members().stream()
                .map(PlayerFind::uuid)
                .flatMap(Optional::stream)
                .forEach(member -> {
                    GlowManager.disable(member);
                    Lang.send(member, "glow.disabled", placeholders);
                });

        GlowUpdater.forceUpdateAll(clan);
    }
}