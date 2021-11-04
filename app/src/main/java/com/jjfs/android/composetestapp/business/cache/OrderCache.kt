package com.jjfs.android.composetestapp.business.cache

import android.content.Context
import com.jjfs.android.composetestapp.Database
import com.jjfs.android.composetestapp.Order_entity
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

interface OrderCacheInterface {
    suspend fun getOrder(id: String): Order_entity?
    suspend fun addOrder(id: String, description: String)
    suspend fun deleteOrder(id: String)
}

class OrderCache(driverFactory: DriverFactory): OrderCacheInterface {

    private val query = driverFactory.createDriver()
        .let { driver -> Database(driver).orderDbQueries
    }

    override suspend fun getOrder(id: String): Order_entity {
        return query.getOrder(id).executeAsOne()
    }

    override suspend fun addOrder(id: String, description: String) {
        return query.insert(id, description)
    }

    override suspend fun deleteOrder(id: String) {
        query.removeOrder(id)
    }
}

class DriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "order.db")
    }
}
