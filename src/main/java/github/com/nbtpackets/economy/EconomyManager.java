package github.com.nbtpackets.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import github.com.nbtpackets.CloudClans;

public class EconomyManager {

    private final Economy economy;

    public EconomyManager(Economy economy) {
        this.economy = economy;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return amount <= 0 || economy.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (amount <= 0) return true;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        if (amount <= 0) return true;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public String format(double amount) {
        if (amount <= 0) return "0";
        String formatStr = CloudClans.getInstance().getConfig().getString("settings.cost-format", "%.0f");
        return String.format(formatStr, amount);
    }
}