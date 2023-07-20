package io.github.yeandyson.rock_paper_scissors.game.rpc

import org.bukkit.entity.Player

data class RPCPlayerData(
    val player: Player,
    var choiceRpc: RPCData?,
    val rpcGui: RPCGui,
    var ready: Boolean,
    var wins: Int
)
