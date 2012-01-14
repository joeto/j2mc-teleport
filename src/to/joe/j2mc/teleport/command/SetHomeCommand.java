package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;
import to.joe.j2mc.teleport.Teleport_Manager;

public class SetHomeCommand extends MasterCommand {

    public SetHomeCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer && player.hasPermission("j2mc.teleport.home.visit")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /sethome name");
            } else {
                Teleport_Manager.tele().addWarp(player.getName(), player.getLocation());
                player.sendMessage(ChatColor.RED + "Home created");
            }
        }
    }
}
