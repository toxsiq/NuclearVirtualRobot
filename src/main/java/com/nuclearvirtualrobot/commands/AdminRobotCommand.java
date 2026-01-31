package com.nuclearvirtualrobot.commands;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminRobotCommand implements CommandExecutor {
    private final RobotActivatorItem activatorItem;

    public AdminRobotCommand(RobotActivatorItem activatorItem) {
        this.activatorItem = activatorItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3 || !args[0].equalsIgnoreCase("criar")) {
            sender.sendMessage(Component.text("Uso: /arobo criar <simples|supremo> <cash|tokens|toxina> [jogador] [quantidade]", NamedTextColor.RED));
            return true;
        }

        RobotType type;
        RobotEconomy economy;
        try {
            type = RobotType.fromInput(args[1]);
            economy = RobotEconomy.fromInput(args[2]);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Component.text("Tipo ou economia inválida.", NamedTextColor.RED));
            return true;
        }

        Player target = null;
        if (args.length >= 4) {
            target = Bukkit.getPlayerExact(args[3]);
            if (target == null) {
                sender.sendMessage(Component.text("Jogador não encontrado.", NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        }

        if (target == null) {
            sender.sendMessage(Component.text("Informe um jogador válido.", NamedTextColor.RED));
            return true;
        }

        int amount = 1;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("Quantidade inválida.", NamedTextColor.RED));
                return true;
            }
        }
        if (amount <= 0) {
            sender.sendMessage(Component.text("Quantidade deve ser maior que zero.", NamedTextColor.RED));
            return true;
        }

        target.getInventory().addItem(activatorItem.createActivator(type, economy, amount));
        sender.sendMessage(Component.text("Ativador enviado para ", NamedTextColor.GREEN)
                .append(Component.text(target.getName(), NamedTextColor.YELLOW)));
        return true;
    }
}
