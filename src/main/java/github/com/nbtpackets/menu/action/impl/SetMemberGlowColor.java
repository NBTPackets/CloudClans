package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.glow.GlowColor;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.perm.ClanPerm;
import github.com.nbtpackets.util.Lang;
import github.com.nbtpackets.player.PlayerMsg;

import java.util.Optional;
import java.util.UUID;

public class SetMemberGlowColor implements MenuAction {

    private final UUID targetUuid;
    private final String colorInput;

    public SetMemberGlowColor(UUID targetUuid, String colorInput) {
        this.targetUuid = targetUuid;
        this.colorInput = colorInput;
    }

    @Override
    public void execute(Player player) {
        ClanManager cm = CloudClans.getInstance().getClanManager();
        cm.getPlayerClan(player.getUniqueId()).ifPresentOrElse(clan -> {
            if (!clan.hasPerm(player.getUniqueId(), ClanPerm.MANAGE_MEMBER_GLOW)) {
                Lang.send(player, "perm.no-perm");
                return;
            }

            if (!clan.members().contains(targetUuid)) {
                PlayerMsg.send(player, "<red>Этот игрок не состоит в вашем клане");
                return;
            }

            parseColor().ifPresentOrElse(
                    color -> {
                        cm.setMemberGlowColor(clan.name(), targetUuid, color);
                        player.updateInventory();
                    },
                    () -> PlayerMsg.send(player, "<red>Неправильный формат цвета: <gold>" + colorInput)
            );
        }, () -> Lang.send(player, "glow.no-clan"));
    }

    private Optional<GlowColor> parseColor() {
        return Optional.ofNullable(colorInput)
                .filter(s -> !s.isBlank())
                .flatMap(s -> s.startsWith("#")
                        ? GlowColor.fromHex(s)
                        : parseRgb(s));
    }

    private Optional<GlowColor> parseRgb(String input) {
        String[] parts = input.split(";");
        if (parts.length != 3) return Optional.empty();
        try {
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return Optional.of(GlowColor.of(r, g, b));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}