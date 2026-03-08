package github.com.nbtpackets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import github.com.nbtpackets.addon.AddonLoader;
import github.com.nbtpackets.api.ClanProvider;
import github.com.nbtpackets.command.ClanCommand;
import github.com.nbtpackets.database.Db;
import github.com.nbtpackets.database.DbCreds;
import github.com.nbtpackets.database.impl.MysqlDb;
import github.com.nbtpackets.database.impl.SqliteDb;
import github.com.nbtpackets.economy.EconomyManager;
import github.com.nbtpackets.glow.listener.GlowBukkitListener;
import github.com.nbtpackets.glow.listener.GlowPacketListener;
import github.com.nbtpackets.level.ClanLevelService;
import github.com.nbtpackets.level.listener.ClanExpListener;
import github.com.nbtpackets.listener.PvpListener;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.menu.MenuListener;
import github.com.nbtpackets.menu.MenuType;
import github.com.nbtpackets.util.Papi;
import github.com.nbtpackets.util.Registrar;
import github.com.nbtpackets.util.lang.Translation;
import github.com.nbtpackets.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class CloudClans extends JavaPlugin {

    @Getter private static CloudClans instance;
    @Getter private Translation translation;
    @Getter private ClanManager clanManager;
    @Getter private EconomyManager economyManager;
    @Getter private ClanLevelService levelService;
    @Getter private AddonLoader addonLoader;
    private Db database;
    private Economy econ;
    private LogUtil log;

    //@Override
    //public void onLoad() {
        //if (Bukkit.getPluginManager().getPlugin("packetevents") != null) {
            //PacketEvents.getAPI().getEventManager().registerListener(new GlowPacketListener(new ClanManager(database)), PacketListenerPriority.NORMAL);
        //}
    //}

    @Override
    public void onEnable() {
        instance = this;
        log = LogUtil.of(this);
        saveDefaultConfig();
        List<String> supportedLangs = List.of("ru", "en", "ua");
        String language = getConfig().getString("language", "ua");
        translation = new Translation(this);
        translation.load(language, supportedLangs);

        createMenuDirectory();
        initDatabase();
        if (!setupEconomy()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        levelService = new ClanLevelService();
        clanManager = new ClanManager(database);
        clanManager.load();
        clanManager.startSaveTask();
        clanManager.startInviteCleanupTask();

        if (Bukkit.getPluginManager().getPlugin("packetevents") != null) {
            PacketEvents.getAPI().getEventManager().registerListener(new GlowPacketListener(clanManager), PacketListenerPriority.NORMAL);
            Registrar.events(this, new GlowBukkitListener(clanManager));
        } else {
            log.warn("Для глоу нужен PacketEvents");
        }

        getServer().getServicesManager().register(ClanProvider.class, clanManager, this, ServicePriority.Normal);

        ClanCommand clanCmd = new ClanCommand(clanManager);
        getCommand("clan").setExecutor(clanCmd);
        getCommand("clan").setTabCompleter(clanCmd);

        Registrar.events(this,
                new PvpListener(clanManager),
                new MenuListener(),
                new ClanExpListener(clanManager, levelService)
        );

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Papi().register();
        }

        addonLoader = new AddonLoader(getDataFolder());
        addonLoader.findAndLoad();
        addonLoader.enableAll();
        log.info("Плагин запущен");
    }

    @Override
    public void onDisable() {
        if (addonLoader != null) {
            try {
                addonLoader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (database != null) database.close();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) return false;
        econ = rsp.getProvider();
        economyManager = new EconomyManager(econ);
        return true;
    }

    private void createMenuDirectory() {
        File menuDir = new File(getDataFolder(), "menu");
        menuDir.mkdirs();
        Arrays.stream(MenuType.values()).forEach(type -> {
            String fileName = type.getConfigName() + ".yml";
            File file = new File(menuDir, fileName);
            if (!file.exists()) saveResource("menu/" + fileName, false);
        });
    }

    private void initDatabase() {
        String type = getConfig().getString("settings.database.type", "sqlite").toLowerCase();
        if (type.equals("mysql")) {
            ConfigurationSection sec = getConfig().getConfigurationSection("settings.database.mysql");
            DbCreds creds = new DbCreds(
                    sec.getString("host"),
                    sec.getInt("port"),
                    sec.getString("database"),
                    sec.getString("username"),
                    sec.getString("password")
            );
            database = new MysqlDb(creds);
            log.info("Используется MySQL");
        } else {
            database = new SqliteDb();
            log.info("Используется SQLite");
        }
    }
}