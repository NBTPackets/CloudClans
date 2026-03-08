package github.com.nbtpackets.level.model;

public record ClanLevel(
        int level,
        int maxMembers,
        long requiredExp,
        LevelUp levelUp
) {}