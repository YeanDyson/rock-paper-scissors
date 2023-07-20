package io.github.yeandyson.rock_paper_scissors.game

import org.bukkit.entity.Player

data class Invitation (
    val requester: Player,
    val receiver: Player,
    val bestOf: Int
)