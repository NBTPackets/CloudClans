package github.com.nbtpackets.api.menu;

import org.bukkit.entity.Player;
import github.com.nbtpackets.menu.Menu;
import github.com.nbtpackets.menu.MenuManager;
import github.com.nbtpackets.model.Clan;
import github.com.nbtpackets.player.PlayerMsg;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MenuRegistry {

    private static final Map<String, Function<Clan, Menu>> factories = new HashMap<>();

    public static void register(String id, Function<Clan, Menu> factory) {
        factories.put(id.toLowerCase(), factory);
    }

    public static void unregister(String id) {
        factories.remove(id.toLowerCase());
    }

    public static boolean isRegistered(String id) {
        return factories.containsKey(id.toLowerCase());
    }

    public static void open(Player player, String menuId, Clan clan) {
        Optional.ofNullable(factories.get(menuId.toLowerCase()))
                .map(f -> f.apply(clan))
                .ifPresentOrElse(
                        menu -> MenuManager.openMenu(player, menu),
                        () -> PlayerMsg.send(player, "<red>Меню не найдено: " + menuId)
                );
    }

    public static Optional<Menu> create(String id, Clan clan) {
        return Optional.ofNullable(factories.get(id.toLowerCase()))
                .map(f -> f.apply(clan));
    }

    public static Map<String, Function<Clan, Menu>> getAll() {
        return Collections.unmodifiableMap(factories);
    }

    public static void clear() {
        factories.clear();
    }
}