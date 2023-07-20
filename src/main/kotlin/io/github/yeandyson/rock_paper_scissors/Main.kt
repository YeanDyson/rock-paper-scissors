package io.github.yeandyson.rock_paper_scissors

import io.github.yeandyson.rock_paper_scissors.command.RPCCommand
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        logger.info("start")

        getCommand("rpc")?.setExecutor(RPCCommand())
    }

    override fun onDisable() {
        logger.info("d")
    }
}