package github.com.nbtpackets.menu.action.impl;

import github.com.nbtpackets.menu.action.MenuAction;

public class Player implements MenuAction {
    private final String command;

    public Player(String command) {
        this.command = command;
    }

    @Override
    public void execute(org.bukkit.entity.Player player) {
        if (command == null || command.isEmpty()) return;
        String cmd = command.startsWith("/")
                ? command.substring(1)
                : command;

        player.performCommand(cmd);
    }
}