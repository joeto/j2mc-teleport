package to.joe.j2mc.teleport.command.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class TeleportBanCommand extends MasterCommand {

    public TeleportBanCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length == 1) {
            final String targetName = args[0];
            Player target = null;
            try {
                target = J2MC_Manager.getVisibility().getPlayer(targetName, sender);
            } catch (final BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return;
            }
            final String properTargetName = target.getName();
            if (((J2MC_Teleport) this.plugin).tpBannedPlayers.containsKey(properTargetName)) {
                this.plugin.getServer().getScheduler().cancelTask(((J2MC_Teleport) this.plugin).tpBannedPlayers.remove(properTargetName));
                target.sendMessage(ChatColor.RED + "Your teleport privileges have been restored.");
                J2MC_Manager.getCore().adminAndLog(ChatColor.RED + sender.getName() + " restored " + properTargetName + "'s teleport privileges.");
            } else {
                Integer id;
                id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        ((J2MC_Teleport) plugin).tpBannedPlayers.remove(properTargetName);
                        J2MC_Manager.getCore().adminAndLog(ChatColor.RED + properTargetName + "'s teleport privileges have been restored.");
                        Player p = plugin.getServer().getPlayer(properTargetName);
                        if (p != null) {
                            p.sendMessage(ChatColor.RED + "Your teleport privileges have been restored.");
                        }
                    }
                }, 6000L);
                ((J2MC_Teleport) this.plugin).tpBannedPlayers.put(target, id);
                target.sendMessage(ChatColor.RED + "Your teleport privileges have been temporarly revoked.");
                J2MC_Manager.getCore().adminAndLog(ChatColor.RED + sender.getName() + " revoked " + properTargetName + "'s teleport privileges.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /tpban playername");
        }
    }
}
