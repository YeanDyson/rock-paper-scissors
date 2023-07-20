package io.github.yeandyson.rock_paper_scissors.game

import io.github.yeandyson.rock_paper_scissors.game.rpc.RPCData
import io.github.yeandyson.rock_paper_scissors.game.rpc.RPCRoom
import org.bukkit.Material

object RoomManager {
    private val rooms = mutableListOf<RPCRoom>()

    private var rock: RPCData = RPCData("rock", Material.STONE, null, null)
    private var paper: RPCData = RPCData("paper", Material.PAPER, null, null)
    private var scissors: RPCData = RPCData("scissors", Material.SHEARS, null, null)
    private val defaultRule: List<RPCData>

    init {
        rock.canDefeat = scissors
        rock.defeatedBy = paper
        paper.canDefeat = rock
        paper.defeatedBy = scissors
        scissors.canDefeat = paper
        scissors.defeatedBy = rock

        defaultRule = listOf(rock, paper, scissors)
    }

    fun createRoom(bestOf: Int): RPCRoom {
        val room = RPCRoom(defaultRule, bestOf)
        rooms.add(room)
        return room
    }

    fun removeRoom(room: RPCRoom) {
        rooms.remove(room)
    }
}