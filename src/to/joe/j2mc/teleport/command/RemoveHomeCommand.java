package to.joe.j2mc.teleport.command;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class RemoveHomeCommand extends MasterCommand<J2MC_Teleport> {

    public RemoveHomeCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            if (args.length == 1) {
                final HashMap<String, Location> warps = this.plugin.getWarps(player.getName());
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "You have no homes available.");
                    return;
                }
                if (warps.containsKey(args[0])) {
                    plugin.deleteWarp(player.getName(), args[0]);
                    player.sendMessage(ChatColor.RED + "Home removed.");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any homes by the name of '" + ChatColor.BOLD + args[0] + ChatColor.RESET + ChatColor.RED + "'");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /removehome <homename>");
            }
        }
    }

}
