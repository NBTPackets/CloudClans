package github.com.nbtpackets.menu.action.impl;

import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.api.menu.MenuRegistry;
import github.com.nbtpackets.menu.Menu;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.menu.action.MenuAction;
import github.com.nbtpackets.menu.impl.GlowMenu;
import github.com.nbtpackets.menu.impl.MainMenu;
import github.com.nbtpackets.menu.impl.PlayerGlowList;
import github.com.nbtpackets.menu.impl.PlayerList;
import github.com.nbtpackets.player.PlayerMsg;

import java.util.Optional;

public class OpenMenu implements MenuAction {
    private final String menuId;

    public OpenMenu(String menuId) {
        this.menuId = menuId;
    }

    @Override
    public void execute(Player player) {
        createMenuById(player, menuId)
                .ifPresentOrElse(
                        menu -> MenuManager.openMenu(player, menu),
                        () -> PlayerMsg.send(player, "<red>Меню <gold>" + menuId + " <red>не найдено")
                );
    }

    private Optional<Menu> createMenuById(Player player, String id) {
        String lower = id.toLowerCase();

        Optional<Menu> coreMenu = switch (lower) {
            case "main-menu" -> CloudClans.getInstance().getClanManager()
                    .getPlayerClan(player.getUniqueId())
                    .map(MainMenu::new);
            case "player-list" -> CloudClans.getInstance().getClanManager()
                    .getPlayerClan(player.getUniqueId())
                    .map(PlayerList::new);
            case "glow" -> CloudClans.getInstance().getClanManager()
                    .getPlayerClan(player.getUniqueId())
                    .map(GlowMenu::new);
            case "player-glow-list" -> CloudClans.getInstance().getClanManager()
                    .getPlayerClan(player.getUniqueId())
                    .map(PlayerGlowList::new);
            default -> Optional.empty();
        };

        if (coreMenu.isPresent()) return coreMenu;

        return CloudClans.getInstance().getClanManager()
                .getPlayerClan(player.getUniqueId())
                .flatMap(clan -> MenuRegistry.create(lower, clan));
    }
}