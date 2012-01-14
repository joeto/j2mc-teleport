package to.joe.j2mc.teleport.command;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;
import to.joe.j2mc.teleport.Teleport_Manager;

public class HomeCommand extends MasterCommand {

    public HomeCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer && player.hasPermission("j2mc.teleport.home.visit")) {
            if (args.length == 0) {
                final HashMap<String, Location> warps = Teleport_Manager.tele().getWarps(player.getName());
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "You have no homes available.");
                    player.sendMessage(ChatColor.RED + "Use the command /sethome");
                } else {
                    final StringBuilder homesList = new StringBuilder();
                    final boolean first = true;
                    for (final String warpName : warps.keySet()) {
                        if (!first) {
                            homesList.append(", ");
                        }
                        homesList.append(warpName);
                    }
                    player.sendMessage(ChatColor.RED + "Homes: " + ChatColor.WHITE + homesList);
                    player.sendMessage(ChatColor.RED + "To go to a home, say /home homename");
                }
            } else {
                final Location location = Teleport_Manager.tele().getNamedWarp(player.getName(), args[0]);
                if (location != null) {
                    player.sendMessage(ChatColor.RED + "Whoosh!");
                    Teleport_Manager.tele().teleport(player, location);
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist. For a list, say /home");
                }
            }
        }
    }
}
