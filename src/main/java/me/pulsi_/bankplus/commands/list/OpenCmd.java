package me.pulsi_.bankplus.commands.list;

import me.pulsi_.bankplus.bankSystem.Bank;
import me.pulsi_.bankplus.bankSystem.BankUtils;
import me.pulsi_.bankplus.commands.BPCommand;
import me.pulsi_.bankplus.utils.texts.BPArgs;
import me.pulsi_.bankplus.utils.texts.BPMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class OpenCmd extends BPCommand {

    public OpenCmd(FileConfiguration commandsConfig, String... aliases) {
        super(commandsConfig, aliases);
    }

    @Override
    public List<String> defaultUsage() {
        return Collections.singletonList("%prefix% &cUsage: &7/bank open <bankName>");
    }

    @Override
    public int defaultConfirmCooldown() {
        return 0;
    }

    @Override
    public List<String> defaultConfirmMessage() {
        return Collections.emptyList();
    }

    @Override
    public int defaultCooldown() {
        return 0;
    }

    @Override
    public List<String> defaultCooldownMessage() {
        return Collections.emptyList();
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public boolean skipUsageWarn() {
        return true;
    }

    @Override
    public boolean preCmdChecks(CommandSender s, String[] args) {
        Bank bank = BankUtils.getBank(getPossibleBank(args, 1));
        if (!BankUtils.exist(bank, s)) return false;
        if (!BankUtils.isAvailable(bank, (Player) s)) {
            BPMessages.send(s, "Cannot-Access-Bank");
            return false;
        }
        return true;
    }

    @Override
    public void onExecution(CommandSender s, String[] args) {
        BankUtils.getBank(getPossibleBank(args, 1)).getBankGui().openBankGui((Player) s);
    }

    @Override
    public List<String> tabCompletion(CommandSender s, String[] args) {
        if (args.length == 2)
            return BPArgs.getArgs(args, BankUtils.getAvailableBankNames((Player) s));
        return null;
    }
}