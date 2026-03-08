package github.com.nbtpackets.command.sub;

import org.bukkit.entity.Player;

public interface SubCommand {
    void execute(Player player, String[] args);
    String getName();
}