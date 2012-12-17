package to.joe.j2mc.teleport.command.admin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

import java.util.HashMap;

public class HomeInvasionCommand extends MasterCommand<J2MC_Teleport> {

    public HomeInvasionCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /homeinvasion <target player>");
                sender.sendMessage(ChatColor.RED + "Usage: /homeinvasion <target player> <target home>");
                return;
            }
            if (args.length == 1) {
                final String target = args[0];
                this.plugin.warpLoad(target);
                final HashMap<String, Location> warps = this.plugin.getWarps(target);
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "User has no homes.");
                } else {
                    final StringBuilder homesList = new StringBuilder();
                    for (final String warpName : warps.keySet()) {
                        homesList.append(warpName);
                        homesList.append(", ");
                    }
                    homesList.setLength(homesList.length() - 2);
                    player.sendMessage(ChatColor.RED + target + "'s homes: " + ChatColor.WHITE + homesList);
                    player.sendMessage(ChatColor.RED + "To go to a home, say /hi " + target + " home");
                }
            } else {
                final String target = args[0];
                this.plugin.warpLoad(target);
                final String home = args[1];
                final Location location = this.plugin.getNamedWarp(target, home);
                if (location != null) {
                    player.sendMessage(ChatColor.RED + "Whoosh!");
                    this.plugin.teleport(player, location);
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist. For a list, say /hi " + target);
                }
            }
        }
    }
}
