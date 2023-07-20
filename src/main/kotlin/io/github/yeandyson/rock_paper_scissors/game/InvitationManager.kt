package io.github.yeandyson.rock_paper_scissors.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object InvitationManager {
    private val plugin = Bukkit.getPluginManager().getPlugin("rock_paper_scissors")!!
    private val invitations = mutableMapOf<UUID, Invitation>()

    private fun findRequester(player: Player): Boolean {
        return invitations.any { it.value.requester == player }
    }

    private fun findReceiver(player: Player): Boolean {
        return invitations.any { it.value.receiver == player }
    }

    fun acceptInvitation(uuid: UUID) {
        val invitation = invitations[uuid] ?: return

        invitation.requester.sendMessage(Component.text("상대방이 대결을 수락했어요"))
        invitation.receiver.sendMessage(Component.text("대결을 수락했서요"))

        RoomManager.createRoom(invitation.bestOf).apply {
            join(invitation.requester)
            join(invitation.receiver)
        }

        invitations.remove(uuid)
    }

    fun declineInvitation(uuid: UUID) {
        val invitation = invitations[uuid] ?: return

        invitation.requester.sendMessage(Component.text("상대방이 대결을 거절했어요"))
        invitation.receiver.sendMessage(Component.text("대결을 거절했어요"))

        invitations.remove(uuid)
    }

    fun sendInvitation(requester: Player, receiver: Player, bestOf: Int = 3) {
        if (requester == receiver) {
            requester.sendMessage(Component.text("자신한테 대결을 요청할수 없어요"))
            return
        }

        if (findRequester(requester)) {
            requester.sendMessage(Component.text("당신은 이미 대결중이거나 요청중이에요"))
            return
        }

        if (findReceiver(receiver)) {
            requester.sendMessage(Component.text("상대방은 이미 대결중이거나 요청중이에요"))
            return
        }

        val uuid = UUID.randomUUID()
        invitations[uuid] = Invitation(requester, receiver, bestOf)

        val acceptComponent = Component.text("[수락]")
            .color(TextColor.color(0, 255, 0))
            .clickEvent(ClickEvent.runCommand("/rpc accept $uuid"))


        val declineComponent = Component.text("[거절]")
            .color(TextColor.color(255, 0, 0))
            .clickEvent(ClickEvent.runCommand("/rpc decline $uuid"))


        receiver.sendMessage(Component.text("대결 신청이 왔스요"))
        receiver.sendMessage(Component.text("------------------"))
        receiver.sendMessage(acceptComponent.append(Component.text("  ").append(declineComponent)))

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (invitations.containsKey(uuid)) {
                requester.sendMessage(Component.text("요청이 만료되었어요"))
                invitations.remove(uuid)
            }
        }, 600L)
    }
}