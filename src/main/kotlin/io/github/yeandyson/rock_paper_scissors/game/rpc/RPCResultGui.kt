package io.github.yeandyson.rock_paper_scissors.game.rpc

import io.github.yeandyson.rock_paper_scissors.game.Item
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class RPCResultGui: Listener {
    private val plugin = Bukkit.getPluginManager().getPlugin("rock_paper_scissors")!!
    private val inventory: Inventory = Bukkit.createInventory(null, 9, Component.text("결과"))

    private var victor: RPCPlayerData? = null
    private var defeated: RPCPlayerData? = null
    private var rpcRoom: RPCRoom
    private var player: Player
    private var isDraw = false
    private var isClosed = false

    private val greenPanel = Item.createItem(Material.GREEN_STAINED_GLASS_PANE, Component.text("  "), lore = arrayOf(Component.text("gui")))
    private val bluePanel = Item.createItem(Material.BLUE_STAINED_GLASS_PANE, Component.text("  "), lore = arrayOf(Component.text("gui")))

    constructor(victor: RPCPlayerData, defeated: RPCPlayerData, rpcRoom: RPCRoom, player: Player) {
        this.victor = victor
        this.defeated = defeated
        this.rpcRoom = rpcRoom
        this.player = player
        this.isDraw = false
        initializeItem()
    }

    constructor(rpcRoom: RPCRoom, player: Player) {
        this.rpcRoom = rpcRoom
        this.player = player
        this.isDraw = true
        initializeItem()
    }

    private fun initializeItem() {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        start()
    }

    private fun start() {
        player.openInventory(inventory)

        val playerData = rpcRoom.players[player]!!

        inventory.setItem(3, Item.createItem(playerData.choiceRpc!!.mainItem, Component.text(playerData.choiceRpc!!.name), lore = arrayOf(
            Component.text(player.name),
            Component.text("gui")
        )))

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            val rivalPlayer = rpcRoom.players.entries.first { it.key != player }
            inventory.setItem(5, Item.createItem(rivalPlayer.value.choiceRpc!!.mainItem, Component.text(rivalPlayer.value.choiceRpc!!.name), lore = arrayOf(
                Component.text(rivalPlayer.key.name),
                Component.text("gui")
            )))
        }, 20L)

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            val playerWins = victor == rpcRoom.players[player]
            val panelRange = if (playerWins) 0..2 else 6..8
            val panelColor = if (isDraw) bluePanel else greenPanel

            if (!isDraw) {
                for (i in panelRange) {
                    inventory.setItem(i, panelColor)
                }
            } else {
                for (i in 0..2) {
                    inventory.setItem(i, panelColor)
                }
                for (i in 6..8) {
                    inventory.setItem(i, panelColor)
                }
            }
        }, 40L)

        Bukkit.getScheduler().runTaskLater(plugin, { _ -> end() }, 40L)
    }

    private fun end() {
        val playerData = rpcRoom.players[player]!!
        isClosed = true
        playerData.choiceRpc = null
        playerData.ready = false

        if (playerData.wins == rpcRoom.bestOf) {
            if (rpcRoom.isClosed) return
            rpcRoom.endRoom()
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, { _ -> playerData.rpcGui.openGui() }, 10L)
        }

        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory !== inventory) return
        e.isCancelled = true
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.inventory !== inventory) return
        if (rpcRoom.isClosed) return
        if (isClosed) return

        HandlerList.unregisterAll(this)
        rpcRoom.surrenderRoom(player)
    }
}