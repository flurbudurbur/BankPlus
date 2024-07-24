package me.pulsi_.bankplus.placeholders;

import me.pulsi_.bankplus.placeholders.list.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BPPlaceholderUtil {

    public static List<BPPlaceholder> registerVariations(List<BPPlaceholder> placeholders) {
        List<BPPlaceholder> registeredPlaceholders = new ArrayList<>();
        for (BPPlaceholder placeholder : placeholders) {
            // deconstruct the placeholder into parts to register all variations
            if (placeholder.hasVariables()) {
                List<List<String>> parts = compileParts(placeholder.getIdentifier());
                List<String> variations = compileVariants(parts, 0);
                for (String variation : variations) {
                    registeredPlaceholders.add(new BPPlaceholder() {
                        @Override
                        public String getIdentifier() {
                            return variation;
                        }

                        @Override
                        public String getPlaceholder(Player p, String target, String identifier) {
                            return placeholder.getPlaceholder(p, target, identifier);
                        }

                        @Override
                        public boolean hasPlaceholders() {
                            return placeholder.hasPlaceholders();
                        }

                        @Override
                        public boolean hasVariables() {
                            return placeholder.hasVariables();
                        }
                    });
                }
            }
        }
        return registeredPlaceholders;
    }

    public static List<String> getRegisteredPlaceholderIdentifiers(List<BPPlaceholder> placeholders) {
        List<String> registeredPlaceholderIdentifiers = new ArrayList<>();
        for (BPPlaceholder placeholder : registerVariations(placeholders)) {
            registeredPlaceholderIdentifiers.add(placeholder.getIdentifier());
        }
        return registeredPlaceholderIdentifiers;
    }

    public static BPPlaceholder parsePlaceholderPlaceholders(List<BPPlaceholder> placeholders, String identifier) {
        // compile a list of special placeholders
        for (BPPlaceholder placeholder : placeholders) {
            if (placeholder.getIdentifier().matches(".*<.*?>.*")) {
                String regex = placeholder.getRegex(placeholder.getIdentifier());
                if (identifier.matches(regex)) {
                    return placeholder;
                }
            }
        }
        return null;
    }

    public static List<List<String>> compileParts(String input) {
        List<List<String>> result = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        boolean insideBrackets = false;

        for (char c : input.toCharArray()) {
            if (c == '[' || c == ']' || (c == '_' && !insideBrackets)) {
                if (!currentSegment.isEmpty()) {
                    result.add(c == ']' ? Arrays.asList(currentSegment.toString().split("/")) : List.of(currentSegment.toString()));
                    currentSegment.setLength(0);
                }
                insideBrackets = (c == '[');
            } else {
                currentSegment.append(c);
            }
        }

        if (!currentSegment.isEmpty()) {
            result.add(List.of(currentSegment.toString()));
        }

        return result;
    }

    public static List<String> compileVariants(List<List<String>> parsed, int i) {
        if (i >= parsed.size()) {
            return Collections.singletonList("");
        }

        List<String> result = new ArrayList<>();
        List<String> currentSegment = parsed.get(i);
        List<String> nextVariants = compileVariants(parsed, i + 1);

        for (String segment : currentSegment) {
            for (String variant : nextVariants) {
                result.add(segment + (variant.isEmpty() ? "" : "_" + variant));
            }
        }

        return result;
    }

    public static List<String> getRecompiledParts(List<List<String>> parts, List<List<String>> oParts) {
        List<String> reParts = new ArrayList<>();
        List<List<String>> oPartsCopy = new ArrayList<>(oParts);
        for (List<String> part : parts) {
            if (part.size() > 1) {
                String current = "", first = oPartsCopy.get(0).toString().replaceAll("[\\[\\]/]", "");
                for (String p : part) {
                    if (p.startsWith(first) && !p.equals(current)) {
                        current = p;
                        if (current.equalsIgnoreCase(p)) {
                            reParts.add(p);
                            int l = p.split("_").length;
                            while (l > 0) {
                                oPartsCopy.remove(0);
                                l--;
                            }
                        }
                    }
                }
            } else {
                reParts.add(part.get(0));
                oPartsCopy.remove(0);
            }
        }
        return reParts;
    }

    private static final List<BPPlaceholder> placeholders = new ArrayList<>();
    public static void registerPlaceholders() {
        placeholders.clear();

        placeholders.add(new BalancePlaceholder());
        placeholders.add(new BankTopPlaceholder());
        placeholders.add(new BankTopPositionPlaceholder());
        placeholders.add(new CapacityPlaceholder());
        placeholders.add(new DebtPlaceholder());
        placeholders.add(new TaxesPlaceholder());
        placeholders.add(new InterestCooldownMillisPlaceholder());
        placeholders.add(new InterestCooldownPlaceholder());
        placeholders.add(new InterestRatePlaceholder());
        placeholders.add(new LevelPlaceholder());
        placeholders.add(new NextInterestPlaceholder());
        placeholders.add(new NextLevelPlaceholder());
        placeholders.add(new NextOfflineInterestPlaceholder());
        placeholders.add(new OfflineInterestRatePlaceholder());
        placeholders.add(new CalculatePercentagePlaceholder());
        placeholders.add(new CalculateTaxesPlaceholder());
        placeholders.add(new NamePlaceholder());
        placeholders.add(new NextLevelCompoundPlaceholder());

        List<BPPlaceholder> variablePlaceholders = BPPlaceholderUtil.registerVariations(placeholders);
        placeholders.addAll(variablePlaceholders);

        List<BPPlaceholder> orderedPlaceholders = new ArrayList<>(), copy = new ArrayList<>(placeholders);
        while (!copy.isEmpty()) {
            BPPlaceholder longest = null;

            int highestLength = 0;
            for (BPPlaceholder placeholder : copy) {
                String identifier = placeholder.getIdentifier();
                int length = identifier.length();

                if (length > highestLength) {
                    highestLength = length;
                    longest = placeholder;
                }
            }

            orderedPlaceholders.add(longest);
            copy.remove(longest);
        }

        placeholders.clear();
        placeholders.addAll(orderedPlaceholders);
    }

    public static List<String> getRegisteredPlaceholders() {
        return new ArrayList<>(getRegisteredPlaceholderIdentifiers(placeholders));
    }
}