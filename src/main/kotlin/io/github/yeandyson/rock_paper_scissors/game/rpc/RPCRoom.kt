package io.github.yeandyson.rock_paper_scissors.game.rpc

import io.github.yeandyson.rock_paper_scissors.game.RoomManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.entity.Player

class RPCRoom(val rule: List<RPCData>, val bestOf: Int) {
    val players = mutableMapOf<Player, RPCPlayerData>()
    var isClosed = false

    private fun proceedResult() {
        val rpcPlayerData = players.values.toList()

        val playerOne = rpcPlayerData[0]
        val playerTwo = rpcPlayerData[1]

        if (playerOne.choiceRpc!!.canDefeat == playerTwo.choiceRpc) {
            playerOne.wins++

            players.forEach {
                RPCResultGui(playerOne, playerTwo, this, it.key)
            }
        } else if (playerOne.choiceRpc!!.defeatedBy == playerTwo.choiceRpc) {
            playerTwo.wins++

            players.forEach {
                RPCResultGui(playerTwo, playerOne, this, it.key)
            }
        } else {
            players.forEach {
                RPCResultGui(this, it.key)
            }
        }
    }

    fun join(player: Player) {
        players[player] = RPCPlayerData(player, null, RPCGui(this, player), false, 0)
    }

    fun leave(player: Player) {
        players.remove(player)
    }

    fun getPlayers(): List<Player> {
        return players.keys.toList()
    }

    fun setPlayerChoiceRpc(player: Player, rpcMaterial: Material) {
        for (rpcData in rule) {
            if (rpcData.mainItem == rpcMaterial) {
                players[player]?.choiceRpc = rpcData
            }
        }
    }

    fun setReady(player: Player, ready: Boolean) {
        var readyPlayer = 0

        players[player]?.ready = ready

        players.forEach {
            if (it.value.ready) readyPlayer++
            it.value.rpcGui.dataRead()
        }

        if (readyPlayer == players.size) {
            players.forEach {
                it.value.rpcGui.isResult = true
            }
            proceedResult()
        }
    }

    private fun showGameResult() {
        val maxScorePlayer = players.maxByOrNull { it.value.wins }!!
        val minScorePlayer = players.minByOrNull { it.value.wins }!!
        val maxPlayerTitle = Title.title(Component.text(maxScorePlayer.key.name).color(TextColor.color(0xfffff)), Component.text("you win").color(TextColor.color(0xfffff)))
        val minPlayerTitle = Title.title(Component.text(maxScorePlayer.key.name).color(TextColor.color(0xe93b3b)), Component.text("you lose").color(TextColor.color(0xe93b3b)))

        maxScorePlayer.key.showTitle(maxPlayerTitle)
        maxScorePlayer.key.sendMessage(Component.text("당신이 이겼어요"))
        minScorePlayer.key.showTitle(minPlayerTitle)
        minScorePlayer.key.sendMessage(Component.text("상대방이 이겼어요"))
    }

    fun endRoom() {
        isClosed = true
        players.forEach {
            it.key.closeInventory()
            it.value.rpcGui.end()
        }
        showGameResult()
        RoomManager.removeRoom(this)
    }

    fun surrenderRoom(onSurrenderPlayer: Player) {
        isClosed = true

        val otherPlayerEntry = players.entries.first { it.key != onSurrenderPlayer }
        otherPlayerEntry.key.closeInventory()
        otherPlayerEntry.value.rpcGui.end()

        RoomManager.removeRoom(this)
    }
}