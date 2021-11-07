package net.d3b8g.landbord.cache

import java.util.*
import kotlin.collections.ArrayList

object AppCache {

    fun Date.deleteBookingInfoByDate() {
        dateCache.filterNot { it.uid != findDateCache() }
    }

    fun Date.haveDateCache(): Boolean {
        for (i in dateCache) {
            if (this.after(i.dateStart) && this.before(i.dateEnd))
                return true
        }
        return false
    }

    fun Date.findDateCache(): Int {
        for (i in dateCache) {
            if (this.after(i.dateStart) && this.before(i.dateEnd))
                return i.uid
        }
        return 0
    }

    var dateCache: ArrayList<DataCache> = ArrayList()

    data class DataCache(val dateStart: Date, val dateEnd: Date, val uid: Int)
}