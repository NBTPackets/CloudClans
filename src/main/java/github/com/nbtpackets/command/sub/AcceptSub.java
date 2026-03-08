package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerFind;

import java.util.Map;

@RequiredArgsConstructor
public class AcceptSub implements SubCommand {

    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        if (clanManager.getPlayerClan(player.getUniqueId()).isPresent()) {
            Lang.send(player, "create.already-in-clan");
            return;
        }

        if (args.length < 2) {
            Lang.send(player, "accept.usage");
            return;
        }

        String clanName = args[1];

        if (!clanManager.hasActiveInvite(player.getUniqueId(), clanName)) {
            Lang.send(player, "accept.no-invite");
            return;
        }

        clanManager.getClan(clanName).ifPresent(clan -> {
            int afterJoin = clan.members().size() + 1;
            if (afterJoin > clan.getMaxMembers()) {
                Lang.send(player, "accept.clan-full", Map.of(
                        "clan", clan.name(),
                        "current", String.valueOf(clan.members().size()),
                        "max", String.valueOf(clan.getMaxMembers())
                ));
                return;
            }

            clanManager.addMember(clanName, player.getUniqueId());
            Lang.send(player, "accept.success", Map.of("clan", clanName));
            PlayerFind.uuid(clan.owner()).ifPresent(owner ->
                    Lang.send(owner, "accept.joined-notify", Map.of("player", player.getName()))
            );
        });
    }

    @Override
    public String getName() {return "accept";}
}