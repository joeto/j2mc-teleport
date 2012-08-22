package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class TeleportCommand extends MasterCommand {

    public TeleportCommand(J2MC_Teleport plugin) {
        super(plugin);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            if (((J2MC_Teleport) this.plugin).tpBannedPlayers.containsKey(sender.getName())) {
                player.sendMessage(ChatColor.RED + "Your teleport privileges have been temporarly revoked.");
                player.sendMessage(ChatColor.RED + "Try again in a few minutes.");
                return;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /tp playername");
                return;
            }
            final String targetName = args[0];
            Player target = null;
            try {
                target = J2MC_Manager.getVisibility().getPlayer(targetName, player);
            } catch (final BadPlayerMatchException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
                return;
            }
            if (((J2MC_Teleport) this.plugin).isProtected(target.getName()) && !player.hasPermission("j2mc.teleport.override")) {
                player.sendMessage(ChatColor.RED + "Cannot teleport to protected player.");
            } else if (target.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatColor.RED + "Can't teleport to yourself");
            } else {
                ((J2MC_Teleport) this.plugin).teleport(player, target.getLocation());
                if (target.isFlying() && player.getAllowFlight()) {
                    player.setFlying(true);
                }
                player.sendMessage("OH GOD I'M FLYING AAAAAAAAH");
                if (target.canSee(player)) {
                    target.sendMessage(ChatColor.BOLD + player.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " teleported to you.");
                }
                this.plugin.getLogger().info(player.getName() + " teleported to " + target.getName());
            }
        }
    }
}
