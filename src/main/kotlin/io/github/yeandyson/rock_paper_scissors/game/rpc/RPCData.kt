package io.github.yeandyson.rock_paper_scissors.game.rpc

import org.bukkit.Material

data class RPCData(
    val name: String,
    val mainItem: Material,
    var canDefeat: RPCData?,
    var defeatedBy: RPCData?
) {
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + mainItem.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RPCData

        if (name != other.name) return false
        return mainItem == other.mainItem
    }
}
