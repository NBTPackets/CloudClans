package github.com.nbtpackets.command.sub;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.menu.impl.MainMenu;
import github.com.nbtpackets.util.Lang;

@RequiredArgsConstructor
public class MenuSub implements SubCommand {

    private final ClanManager clanManager;

    @Override
    public void execute(Player player, String[] args) {
        clanManager.getPlayerClan(player.getUniqueId())
                .ifPresentOrElse(
                        clan -> MenuManager.openMenu(player, new MainMenu(clan)),
                        () -> Lang.send(player, "menu.no-clan")
                );
    }

    @Override
    public String getName() {return "menu";}
}