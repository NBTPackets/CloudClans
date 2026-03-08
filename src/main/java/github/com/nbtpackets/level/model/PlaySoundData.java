package github.com.nbtpackets.level.model;

import org.bukkit.Sound;

public record PlaySoundData(Sound sound, float volume, float pitch) {}