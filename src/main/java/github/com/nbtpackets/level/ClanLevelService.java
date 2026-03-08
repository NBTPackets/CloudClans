package github.com.nbtpackets.level;

import github.com.nbtpackets.level.config.LevelsConfig;
import github.com.nbtpackets.level.model.ClanLevel;
import github.com.nbtpackets.level.model.LevelUp;
import github.com.nbtpackets.model.Clan;
import github.com.nbtpackets.player.PlayerFind;
import github.com.nbtpackets.player.PlayerMsg;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;

public class ClanLevelService {

    private final LevelsConfig config;

    public ClanLevelService() {
        this.config = new LevelsConfig();
    }

    public void reload() {
        config.reload();
    }

    public long expFor(String source) {
        return config.expSources().getOrDefault(source, 0L);
    }

    public int maxMembersForLevel(int level) {
        return Optional.ofNullable(config.levels().floorEntry(level))
                .map(Map.Entry::getValue)
                .map(ClanLevel::maxMembers)
                .orElse(config.defaultMaxMembers());
    }

    public int calculateLevel(long exp) {
        return config.levels().entrySet().stream()
                .filter(e -> exp >= e.getValue().requiredExp())
                .map(Map.Entry::getKey)
                .max(Integer::compareTo)
                .orElse(1);
    }

    public Optional<LevelUp> levelUpFor(int level) {
        return Optional.ofNullable(config.levels().get(level))
                .map(ClanLevel::levelUp);
    }

    public NavigableMap<Integer, ClanLevel> levels() {
        return config.levels();
    }

    public long requiredExpForLevel(int level) {
        return Optional.ofNullable(config.levels().get(level))
                .map(ClanLevel::requiredExp)
                .orElse(0L);
    }

    public void handleLevelUp(Clan clan, int oldLevel) {
        int newLevel = clan.level();

        Map<String, String> placeholders = Map.of(
                "clan", clan.name(),
                "level", String.valueOf(newLevel),
                "max_members", String.valueOf(clan.getMaxMembers())
        );

        for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {
            levelUpFor(lvl).ifPresent(levelUp -> {
                clan.members().stream()
                        .map(PlayerFind::uuid)
                        .flatMap(Optional::stream)
                        .forEach(member -> {
                            levelUp.messages().forEach(msg ->
                                    PlayerMsg.send(member, msg, placeholders)
                            );
                            levelUp.sound().ifPresent(s ->
                                    member.playSound(member.getLocation(), s.sound(), s.volume(), s.pitch())
                            );
                        });
            });
        }
    }
}