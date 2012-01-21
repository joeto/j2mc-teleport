package to.joe.j2mc.teleport.command;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;
import to.joe.j2mc.teleport.Teleport_Manager;

public class WarpCommand extends MasterCommand {

    public WarpCommand(J2MC_Teleport plugin) {
        super(plugin);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer && player.hasPermission("j2mc.teleport.warp.visit")) {
            if (args.length == 0) {
                final HashMap<String, Location> warps = Teleport_Manager.tele().getWarps("");
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "No warps available");
                } else {
                    final StringBuilder warpsList = new StringBuilder();
                    final boolean first = true;
                    for (final String warpName : warps.keySet()) {
                        if (!first) {
                            warpsList.append(", ");
                        }
                        warpsList.append(warpName);
                    }
                    player.sendMessage(ChatColor.RED + "Warps: " + ChatColor.WHITE + warpsList);
                    player.sendMessage(ChatColor.RED + "To go to a warp, say /warp warpname");
                }
            } else {
                final Location target = Teleport_Manager.tele().getNamedWarp("", args[0]);
                if ((target != null)) {
                    player.sendMessage(ChatColor.RED + "Welcome to: " + ChatColor.LIGHT_PURPLE + target);
                    J2MC_Manager.getLog().info(ChatColor.AQUA + "Player " + player.getName() + " went to warp " + target);
                    Teleport_Manager.tele().teleport(player, target);
                } else {
                    player.sendMessage(ChatColor.RED + "Warp does not exist. For a list, say /warp");
                }
            }
        }
    }
}
