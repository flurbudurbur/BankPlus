package me.pulsi_.bankplus.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.pulsi_.bankplus.BankPlus;
import me.pulsi_.bankplus.values.ConfigValues;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BPPlaceholders extends PlaceholderExpansion {

    private final List<BPPlaceholder> placeholders = new ArrayList<>();

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Pulsi_";
    }

    @Override
    public String getIdentifier() {
        return "bankplus";
    }

    @Override
    public String getVersion() {
        return BankPlus.INSTANCE().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) return "Player not online";
        String target = ConfigValues.getMainGuiName();

        // catch the target bank name
        Pattern regex = Pattern.compile("_\\{.*?\\}");
        if (regex.matcher(identifier).find()) {
            target = identifier.substring(identifier.indexOf("{") + 1, identifier.indexOf("}"));
            identifier = identifier.replaceAll("_\\{.*?\\}" + regex, "");
        }

        for (BPPlaceholder placeholder : placeholders) {
            if (placeholder.hasPlaceholders()) {
                BPPlaceholder finalPlaceholder = BPPlaceholderUtil.parsePlaceholderPlaceholders(placeholders, identifier);
                if (finalPlaceholder == null) continue;
                return new BPPlaceholder() {

                    @Override
                    public String getIdentifier() {
                        return finalPlaceholder.getIdentifier();
                    }

                    @Override
                    public String getPlaceholder(Player p, String target, String identifier) {
                        return finalPlaceholder.getPlaceholder(p, target, identifier);
                    }

                    @Override
                    public boolean hasPlaceholders() {
                        return finalPlaceholder.hasPlaceholders();
                    }

                    @Override
                    public boolean hasVariables() {
                        return finalPlaceholder.hasVariables();
                    }
                }.getPlaceholder(p, target, identifier);
            } else {
                if (identifier.toLowerCase().startsWith(placeholder.getIdentifier().toLowerCase()))
                    return placeholder.getPlaceholder(p, target, identifier);
            }
        }
        return null;
    }

    public void registerPlaceholders() {
        BPPlaceholderUtil.registerPlaceholders();
    }
}