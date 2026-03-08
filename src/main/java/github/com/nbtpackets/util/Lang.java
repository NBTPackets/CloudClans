package github.com.nbtpackets.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.player.PlayerMsg;

import java.util.Collections;
import java.util.Map;

@UtilityClass
public class Lang {

    public void send(Player player, String key, Map<String, String> placeholders) {
        PlayerMsg.lang(player, CloudClans.getInstance().getTranslation(), key, placeholders);
    }

    public void send(Player player, String key) {
        send(player, key, Collections.emptyMap());
    }
}