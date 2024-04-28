package me.pulsi_.bankplus.placeholders.list;

import me.pulsi_.bankplus.bankSystem.BankUtils;
import me.pulsi_.bankplus.placeholders.BPPlaceholder;
import me.pulsi_.bankplus.values.Values;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class NextLevelCapacityPlaceholder extends BPPlaceholder {

    @Override
    public String getIdentifier() {
        return "next_level_capacity";
    }

    @Override
    public String getPlaceholder(Player p, String target, String identifier) {
        if (!BankUtils.exist(target)) return "&cThe selected bank does not exist.";
        if (!BankUtils.hasNextLevel(target, p)) return Values.CONFIG.getUpgradesMaxedPlaceholder();

        BigDecimal capacity = BankUtils.getCapacity(target, BankUtils.getCurrentLevel(target, p) + 1);
        if (capacity.longValue() <= 0) return Values.CONFIG.getInfiniteCapacityText();

        return getFormat(identifier, capacity);
    }
}