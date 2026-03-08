package github.com.nbtpackets.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.nbtpackets.CloudClans;

public class Papi extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "cloudclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NBTPackets";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("name")) {
            return CloudClans.getInstance().getClanManager()
                    .getPlayerClan(player.getUniqueId())
                    .map(clan -> clan.name())
                    .orElse("");
        }

        return null;
    }
}