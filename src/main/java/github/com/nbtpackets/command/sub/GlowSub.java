package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.glow.manager.GlowManager;
import github.com.nbtpackets.glow.manager.GlowUpdater;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.model.Clan;
import github.com.nbtpackets.perm.ClanPerm;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerFind;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class GlowSub implements SubCommand {

    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId())
                .ifPresentOrElse(
                        clan -> processGlowToggle(player, clan),
                        () -> Lang.send(player, "glow.no-clan")
                );
    }

    private void processGlowToggle(Player player, Clan clan) {
        if (!clan.hasPerm(player.getUniqueId(), ClanPerm.MANAGE_GLOW)) {
            Lang.send(player, "perm.no-perm");
            return;
        }
        toggleGlow(player, clan);
    }

    private void toggleGlow(Player player, Clan clan) {
        boolean newState = !GlowManager.isEnabled(player);
        String messageKey = newState ? "glow.enabled" : "glow.disabled";
        Map<String, String> placeholders = Map.of("clan", clan.name());

        clan.members().stream()
                .map(PlayerFind::uuid)
                .flatMap(Optional::stream)
                .forEach(member -> {
                    if (newState) GlowManager.enable(member);
                    else GlowManager.disable(member);
                    Lang.send(member, messageKey, placeholders);
                });

        GlowUpdater.forceUpdateAll(clan);
    }

    @Override
    public String getName() { return "glow"; }
}