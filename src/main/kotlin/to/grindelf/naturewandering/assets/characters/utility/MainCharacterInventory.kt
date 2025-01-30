package to.grindelf.naturewandering.assets.characters.utility

import to.grindelf.naturewandering.assets.objects.Item

class MainCharacterInventory : Inventory() {

    val items = mutableListOf<Item>()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
    }

    fun hasItem(item: Item): Boolean {
        return items.contains(item)
    }

}