package github.com.nbtpackets.menu.config;

import org.bukkit.configuration.file.YamlConfiguration;
import github.com.nbtpackets.CloudClans;
import github.com.nbtpackets.menu.MenuType;

import java.io.File;
import java.io.IOException;

public class MenuConfig {
    private final File file;
    private YamlConfiguration config;
    public MenuConfig(MenuType type) {
        File menuDir = new File(CloudClans.getInstance().getDataFolder(), "menu");
        if (!menuDir.exists()) {
            menuDir.mkdirs();
        }
        String fileName = type.getConfigName() + ".yml";
        this.file = new File(menuDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                CloudClans.getInstance().saveResource("menu/" + fileName, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration get() {
        return config;
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}