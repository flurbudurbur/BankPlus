package me.pulsi_.bankplus.utils;

import me.pulsi_.bankplus.BankPlus;
import me.pulsi_.bankplus.interest.Interest;
import me.pulsi_.bankplus.managers.EconomyManager;
import me.pulsi_.bankplus.managers.TaskManager;
import me.pulsi_.bankplus.values.Values;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.BigDecimal;

public class BPDebugger {

    private static boolean isChatDebuggerEnabled = false, isGuiDebuggerEnabled = false;

    public static void debugChat(AsyncPlayerChatEvent e) {
        if (!isIsChatDebuggerEnabled()) return;
        String message = e.getMessage();
        String stripMessage = ChatColor.stripColor(message);
        Player p = e.getPlayer();

        BPLogger.log("");
        BPLogger.log("                     &aBank&9Plus&dDebugger&9: &aPLAYER_CHAT");
        BPLogger.info("PlayerName: &a" + p.getName() + " &9(UUID: &a" + p.getUniqueId() + "&9)");
        if (Methods.isDepositing(p)) BPLogger.info("PlayerStatus: &aIS_DEPOSITING");
        else if (Methods.isWithdrawing(p)) BPLogger.info("PlayerStatus: &aIS_WITHDRAWING");
        BPLogger.info("PlayerMessage: &a" + message + "&9 (StrippedMessage: &a" + stripMessage + "&9)");

        boolean isExitMessage = stripMessage.equalsIgnoreCase(Values.CONFIG.getExitMessage());
        BPLogger.info("IsExitMessage: &a" + isExitMessage);
        if (isExitMessage) {
            BPLogger.log("");
            return;
        }

        BPLogger.info("MessageIsNumber: &a" + Methods.isValidNumber(message));
        if (Methods.isValidNumber(message)) {
            BigDecimal mainBalance = BigDecimal.valueOf(BankPlus.getEconomy().getBalance(p));
            BigDecimal bankBalance = EconomyManager.getBankBalance(p);
            BigDecimal messageNumber = new BigDecimal(message);
            BPLogger.info("PlayerMainBalance: &a" + mainBalance);
            BPLogger.info("PlayerBankBalance: &a" + bankBalance);

            boolean hasEnoughMoneyWithdraw = bankBalance.doubleValue() > 0;
            boolean hasEnoughMoneyDeposit = mainBalance.doubleValue() > 0;
            if (Methods.isWithdrawing(p)) BPLogger.info("HasEnoughMoneyToWithdraw: &a" + (bankBalance.doubleValue() > 0));
            else if (Methods.isWithdrawing(p)) BPLogger.info("HasEnoughMoneyToDeposit: &a" + (mainBalance.doubleValue() > 0));

            if (!hasEnoughMoneyWithdraw || !hasEnoughMoneyDeposit) {
                BPLogger.log("");
                return;
            }

            BigDecimal newMainBalance = null;
            BigDecimal newBankBalance = null;
            if (Methods.isWithdrawing(p)) {
                if (messageNumber.doubleValue() > bankBalance.doubleValue()) messageNumber = bankBalance;
                newMainBalance = mainBalance.add(messageNumber);
                newBankBalance = (bankBalance.subtract(messageNumber)).doubleValue() < 0 ? BigDecimal.valueOf(0) : bankBalance.subtract(messageNumber);
                if (messageNumber.doubleValue() > bankBalance.doubleValue()) BPLogger.info("The player &ahas not &9enough money to withdraw. (NumberInsertedLimited: &a" + messageNumber + "&9, BankBalance: &a" + bankBalance + "&9)");
                else BPLogger.info("The player &ahas &9enough money to withdraw. (NumberInsertedLimited: &a" + messageNumber + "&9, BankBalance: &a" + bankBalance + "&9)");
            }
            else if (Methods.isDepositing(p)) {
                if (messageNumber.doubleValue() > mainBalance.doubleValue()) messageNumber = mainBalance;
                newMainBalance = (mainBalance.subtract(messageNumber)).doubleValue() < 0 ? BigDecimal.valueOf(0) : mainBalance.subtract(messageNumber);
                newBankBalance = (bankBalance.add(messageNumber)).doubleValue() > Values.CONFIG.getMaxBankCapacity().doubleValue() ? Values.CONFIG.getMaxBankCapacity() : bankBalance.add(messageNumber);
                if (messageNumber.doubleValue() > mainBalance.doubleValue()) BPLogger.info("The player &ahas not &9enough money to deposit. (NumberInsertedLimited: &a" + messageNumber + "&9, MainBalance: &a" + mainBalance + "&9)");
                else BPLogger.info("The player &ahas &9enough money to deposit. (NumberInsertedLimited: &a" + messageNumber + "&9, MainBalance: &a" + mainBalance + "&9)");
            }
            BPLogger.info("NewBankBalance: &a" + newBankBalance + "&9, NewMainBalance: &a" + newMainBalance + "&9)");
        }
        BPLogger.log("");
    }

    public static void debugInterest() {
        BPLogger.log("");
        BPLogger.log("                     &aBank&9Plus&dDebugger&9: &aINTEREST");
        BPLogger.info("IsInterestActive: &a" + Values.CONFIG.isInterestEnabled());
        if (!Values.CONFIG.isInterestEnabled()) {
            BPLogger.log("");
            return;
        }

        String task = TaskManager.getInterestTask().toString();
        if (task == null) task = "null";
        else task = task.replace("org.bukkit.craftbukkit.", "");

        BPLogger.info("InterestTask: &a" + task + " &9(IsNull: &a" + (TaskManager.getInterestTask() == null) + "&9)");
        BPLogger.info("ServerMilliseconds: &a" + System.currentTimeMillis() + "ms");
        BPLogger.info("InterestCooldownMillis: &a" + Interest.getInterestCooldownMillis() + "ms &9(&a" + Methods.formatTime(Interest.getInterestCooldownMillis()) + "&9)");
        BPLogger.info("InterestDelay: &a" + Values.CONFIG.getInterestDelay() + "ms &9(&a" + Methods.formatTime(Values.CONFIG.getInterestDelay()) + "&9)");
        BPLogger.info("PlayersWaitingInterest: &a" + Bukkit.getOnlinePlayers().size() + " &9(&a" + Bukkit.getOfflinePlayers().length + " Total&9)");
        BPLogger.info("IsOfflineInterest: &a" + Values.CONFIG.isGivingInterestToOfflinePlayers());
        BPLogger.log("");
    }

    public static void debugGui(InventoryClickEvent e) {
        if (!isIsGuiDebuggerEnabled()) return;

        HumanEntity entity = e.getWhoClicked();
        if (!(entity instanceof Player)) return;
        Player p = (Player) entity;
        int slot = e.getSlot() + 1;

        BPLogger.log("");
        BPLogger.log("                       &aBank&9Plus&dDebugger&9: &aGUI");
        BPLogger.info("PlayerName: &a" + p.getName() + " &9(UUID: &a" + p.getUniqueId() + "&9)");
        BigDecimal mainBalance = BigDecimal.valueOf(BankPlus.getEconomy().getBalance(p));
        BigDecimal bankBalance = EconomyManager.getBankBalance(p);
        BPLogger.info("PlayerMainBalance: &a" + mainBalance);
        BPLogger.info("PlayerBankBalance: &a" + bankBalance);
        BPLogger.info("ClickedSlot: &a" + slot);

        String itemPath = null, actionType = null, actionNumber = null;
        boolean hasAction = false;
        for (String key : Values.BANK.getGuiItems().getKeys(false)) {
            ConfigurationSection item = BankPlus.getCm().getConfig("bank").getConfigurationSection("Items." + key);
            if (item == null || slot != item.getInt("Slot")) continue;
            itemPath = item.toString();

            String type = item.getString("Action.Action-Type");
            actionType = type == null ? "Not found." : type;
            String number = item.getString("Action.Amount");
            actionNumber = number == null ? "Not found." : number;
            hasAction = type != null;
        }

        BPLogger.info("ItemPath: &a" + (itemPath == null ? "GuiFiller" : itemPath));
        BPLogger.info("HasAction: &a" + hasAction);
        if (!hasAction) {
            BPLogger.log("");
            return;
        }
        BPLogger.info("ActionType: &a" + actionType);
        boolean isValidNumber = actionNumber.equalsIgnoreCase("ALL") || actionNumber.equalsIgnoreCase("HALF") || Methods.isValidNumber(actionNumber);
        BPLogger.info("ActionNumber: &a" + actionNumber + " &9(IsValidNumber: &a" + Methods.isValidNumber(actionNumber) + "&9, IsValidAction: &a" + isValidNumber + "&9)");
        BPLogger.log("");
    }

    public static void toggleChatDebugger(CommandSender s) {
        if (isChatDebuggerEnabled) s.sendMessage(BPChat.color("&a&lBank&9&lPlus &aYou have &cdeactivated &athe CHAT debugger!"));
        else s.sendMessage(BPChat.color("&a&lBank&9&lPlus &aYou have &2activated &athe CHAT debugger!"));
        isChatDebuggerEnabled = !isChatDebuggerEnabled;
    }

    public static void toggleGuiDebugger(CommandSender s) {
        if (isGuiDebuggerEnabled) s.sendMessage(BPChat.color("&a&lBank&9&lPlus &aYou have &cdeactivated &athe GUI debugger!"));
        else s.sendMessage(BPChat.color("&a&lBank&9&lPlus &aYou have &2activated &athe GUI debugger!"));
        isGuiDebuggerEnabled = !isGuiDebuggerEnabled;
    }

    public static boolean isIsChatDebuggerEnabled() {
        return isChatDebuggerEnabled;
    }

    public static boolean isIsGuiDebuggerEnabled() {
        return isGuiDebuggerEnabled;
    }
}