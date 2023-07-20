package io.github.yeandyson.rock_paper_scissors.command

import io.github.yeandyson.rock_paper_scissors.game.InvitationManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class RPCCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (command.name.equals("rpc", ignoreCase = true)) {
                if (args.isEmpty()) {
                    sender.sendMessage(Component.text("You need to specify the other player's name!"))
                    return true
                }

                if (args[0] == "accept") {
                    InvitationManager.acceptInvitation(UUID.fromString(args[1]))
                    return true
                }

                if (args[0] == "decline") {
                    InvitationManager.declineInvitation(UUID.fromString(args[1]))
                    return true
                }

                val otherPlayer = Bukkit.getPlayer(args[0]) ?: return true
                if (args.size > 1) {
                    InvitationManager.sendInvitation(sender, otherPlayer, args[1].toInt())
                } else {
                    InvitationManager.sendInvitation(sender, otherPlayer)
                }
            }
        }
        return true
    }
}