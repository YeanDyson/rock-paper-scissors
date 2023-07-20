package io.github.yeandyson.rock_paper_scissors.game

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

object Item {
    fun createItem(material: Material, name: Component, amount: Int = 1, damage: Int? = null, enchants: Map<Enchantment, Int>? = null, vararg lore: Component?): ItemStack {
        val item = ItemStack(material, amount)
        val metadata = item.itemMeta
        metadata.displayName(name)

        if (lore.isNotEmpty()) {
            metadata.lore(lore.toMutableList())
        }

        if (damage != null && metadata is Damageable) {
            metadata.damage = damage
        }

        enchants?.forEach { (enchantment, level) ->
            metadata.addEnchant(enchantment, level, true)
        }

        item.itemMeta = metadata
        return item
    }
}