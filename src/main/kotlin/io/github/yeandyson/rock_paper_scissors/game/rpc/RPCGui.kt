package io.github.yeandyson.rock_paper_scissors.game.rpc

import io.github.yeandyson.rock_paper_scissors.game.Item
import io.github.yeandyson.rock_paper_scissors.game.Item.createItem
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
import org.bukkit.inventory.ItemStack

class RPCGui(private val rpcRoom: RPCRoom, private val player: Player): Listener {
    private val plugin = Bukkit.getPluginManager().getPlugin("rock_paper_scissors")!!
    private var inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("가위 바위 보"))
    private val rpcRuleItems = mutableMapOf<RPCData, ItemStack>()

    private val greenPanel = createItem(Material.GREEN_STAINED_GLASS_PANE, Component.text("  "), lore = arrayOf(Component.text("gui")))
    private val ready = createItem(Material.GREEN_STAINED_GLASS_PANE, Component.text("준비"), lore = arrayOf(
        Component.text("누르면 준비 취소가 돼요"),
        Component.text("gui")
    ))
    private val unready = createItem(Material.RED_STAINED_GLASS_PANE, Component.text("준비"), lore = arrayOf(
        Component.text("누르면 준비가 돼요"),
        Component.text("gui")
    ))
    private val rivalReady = createItem(Material.GREEN_STAINED_GLASS_PANE, Component.text("준비"), lore = arrayOf(
        Component.text("상대방은 준비중이에요"),
        Component.text("gui")
    ))
    private val rivalUnready = createItem(Material.RED_STAINED_GLASS_PANE, Component.text("준비"), lore = arrayOf(
        Component.text("상대방은 준비중이 아니에요"),
        Component.text("gui")
    ))

    var isResult = false

    init {
        initializeItem()

        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    private fun initializeItem() {
        val panel = createItem(Material.BLACK_STAINED_GLASS_PANE, Component.text(" "), lore = arrayOf(Component.text("gui")))
        val redPanel = createItem(Material.RED_STAINED_GLASS_PANE, Component.text(" "), lore = arrayOf(Component.text("gui")))

        for (i in 0 .. 26) {
            inventory.setItem(i, panel)
        }
        for (i in 0 .. 2) {
            inventory.setItem(i, redPanel)
        }
        for (i in 6 .. 8) {
            inventory.setItem(i, redPanel)
        }
        for (i in 0..2) {
            val rpcData = rpcRoom.rule[i]

            val item = createItem(rpcData.mainItem, Component.text(rpcRoom.rule[i].name), lore = arrayOf(Component.text("gui")))
            rpcRuleItems[rpcData] = item
            inventory.setItem(i+12, item)
        }

        inventory.setItem(19, unready)
        inventory.setItem(25, redPanel)
        inventory.setItem(22, ItemStack(Material.AIR))

        openGui()
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory !== inventory) return
        val clickedItem = e.currentItem
        if (clickedItem == null || clickedItem.type.isAir) return

        e.isCancelled = true

        if (rpcRuleItems.values.contains(clickedItem)) {
            if (rpcRoom.players[player]!!.ready) return

            rpcRoom.setPlayerChoiceRpc(player, clickedItem.type)
            dataRead()
        }

        if (clickedItem == ready) {
            inventory.setItem(19, unready)
            rpcRoom.setReady(player, false)
        }

        if (clickedItem == unready) {
            if (rpcRoom.players[player]?.choiceRpc == null) return

            inventory.setItem(19, ready)
            rpcRoom.setReady(player, true)
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.inventory !== inventory) return
        if (rpcRoom.isClosed) return
        if (isResult) return

        rpcRoom.surrenderRoom(player)
    }

    fun openGui() {
        if (rpcRoom.isClosed) return

        isResult = false
        player.openInventory(inventory)
        inventory.setItem(19, unready)
        dataRead()
    }

    fun end() {
        HandlerList.unregisterAll(this)
    }

    fun dataRead() {
        if (rpcRoom.players[player]?.choiceRpc == null) {
            inventory.setItem(22, ItemStack(Material.AIR))
        } else {
            inventory.setItem(22, rpcRuleItems[rpcRoom.players[player]!!.choiceRpc])
        }

        rpcRoom.players.forEach {
            if (it.key == player) {
                for (i in 0 until it.value.wins) {
                    inventory.setItem(i, greenPanel)
                }
            } else {
                for (i in 9 - it.value.wins .. 8) {
                    inventory.setItem(i, greenPanel)
                }
                if (it.value.ready) {
                    inventory.setItem(25, rivalReady)
                } else {
                    inventory.setItem(25, rivalUnready)
                }
            }
        }
    }
}