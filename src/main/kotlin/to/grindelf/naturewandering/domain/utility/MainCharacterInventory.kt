package to.grindelf.naturewandering.domain.utility

import to.grindelf.naturewandering.assets.objects.Item

class MainCharacterInventory(
    itemsIds: String?
) : Inventory {

    val items = mutableListOf<Item>()

    init {
        if (!itemsIds.isNullOrEmpty()) {
            val idsAsList = itemsIds.split(",")
            idsAsList.forEach { id ->
                items.add(Item(id))
            }
        }
    }

    override fun toString(): String {
        var inventoryAsString = ""
        items.forEach { item ->
            inventoryAsString += "$item,"
        }

        return "$inventoryAsString;"
    }

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
    }

    fun hasItem(item: Item): Boolean {
        return items.contains(item)
    }

    fun addItem(itemId: String) {
        items.add(Item(itemId))
    }

    fun removeItem(itemId: String) {
        items.remove(Item(itemId))
    }

    fun hasItem(itemId: String): Boolean {
        return items.contains(Item(itemId))
    }

}
