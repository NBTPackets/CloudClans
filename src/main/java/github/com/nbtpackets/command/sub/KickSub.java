package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.perm.ClanPerm;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerFind;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class KickSub implements SubCommand {
    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId()).ifPresentOrElse(clan -> {
            if (!clan.hasPerm(player.getUniqueId(), ClanPerm.KICK_MEMBERS)) {
                Lang.send(player, "perm.no-perm");
                return;
            }

            if (args.length < 2) {
                Lang.send(player, "kick.usage");
                return;
            }

            String targetName = args[1];

            Optional<UUID> targetUUID = clan.members().stream()
                    .map(Bukkit::getOfflinePlayer)
                    .filter(op -> op.getName() != null && op.getName().equalsIgnoreCase(targetName))
                    .map(OfflinePlayer::getUniqueId)
                    .findFirst();

            targetUUID.ifPresentOrElse(uuid -> {
                if (uuid.equals(player.getUniqueId())) {
                    Lang.send(player, "kick.kick-self");
                    return;
                }

                if (clan.isOwner(uuid)) {
                    Lang.send(player, "kick.cannot-kick-leader");
                    return;
                }

                clanManager.removeMember(clan.name(), uuid);
                Lang.send(player, "kick.success", Map.of("player", targetName));
                PlayerFind.uuid(uuid).ifPresent(target ->
                        Lang.send(target, "kick.kicked-notify", Map.of("clan", clan.name()))
                );
            }, () -> Lang.send(player, "kick.not-in-your-clan"));

        }, () -> Lang.send(player, "help.no-clan"));
    }

    @Override
    public String getName() { return "kick"; }
}