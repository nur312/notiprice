package com.notiprice.dao

import com.notiprice.entity.Product
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Types

@Component
class ProductDao(private val jdbcTemplate: JdbcTemplate) {


    fun save(product: Product): Product {
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        val numOfUpdates = jdbcTemplate.update({ connection: Connection ->
            val ps = connection
                .prepareStatement(
                    "insert into $products " +
                            "($name, $price, $currency, $url, $xpath, $priceStr, $lastCheck) " +
                            "values (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            ps.setString(1, product.name)
            ps.setString(2, product.price.toString())
            ps.setString(3, product.currency)
            ps.setString(4, product.url)
            ps.setString(5, product.xpath)
            ps.setString(6, product.priceStr)
            ps.setLong(7, product.lastCheck)
            ps
        }, keyHolder)

        require(numOfUpdates == 1)

        product.id = keyHolder.key as Long

        return product
    }

    fun findByIdOrNull(id: Long): Product? {

        return jdbcTemplate.query(
            "select * from $products where $id = ?",
            arrayOf<Any>(id),
            intArrayOf(Types.BIGINT)
        ) { rs: ResultSet, _: Int ->
            Product(
                rs.getLong(Companion.id),
                rs.getString(name),
                rs.getDouble(price),
                rs.getString(currency),
                rs.getString(url),
                rs.getString(xpath),
                rs.getString(priceStr),
                rs.getLong(lastCheck),
            )
        }.firstOrNull()
    }

    fun update(product: Product) {

        val numOfUpdates = jdbcTemplate.update(
            "update $products " +
                    "set $name = ?, " +
                    "$price = ?, " +
                    "$currency = ?, " +
                    "$url = ?, " +
                    "$xpath = ?, " +
                    "$priceStr = ?, " +
                    "$lastCheck = ? " +
                    "where $id = ?",
            product.name,
            product.price,
            product.currency,
            product.url,
            product.xpath,
            product.priceStr,
            product.lastCheck,
            product.id
        )

        require(numOfUpdates == 1)
    }

    fun delete(productId: Long) {

        val numOfUpdates = jdbcTemplate.update(
            "delete $products where id = ?",
            productId
        )

        require(numOfUpdates == 1)
    }

    fun findAllUserProducts(username: String): List<Product> = jdbcTemplate.query(
        "select * from $products join ${SubscriptionDao.subscriptions} on " +
                "$products.$id = ${SubscriptionDao.subscriptions}.${SubscriptionDao.productId} join ${UserDao.users} on " +
                "${UserDao.users}.${UserDao.chatId} = ${SubscriptionDao.subscriptions}.${SubscriptionDao.chatId} " +
                "where ${UserDao.users}.${UserDao.username} = ?", username
    ) { rs: ResultSet, _: Int ->
        Product(
            rs.getLong(id),
            rs.getString(name),
            rs.getDouble(price),
            rs.getString(currency),
            rs.getString(url),
            rs.getString(xpath),
            rs.getString(priceStr),
            rs.getLong(lastCheck)
        )
    }

    fun findToCheck(timeInterval: Int, limit: Int): List<Product> {

        return jdbcTemplate.query(
            "select * from $products where " +
                    "$lastCheck + ? <= EXTRACT (EPOCH from CURRENT_TIMESTAMP)*1000000 " +
                    "order by $lastCheck limit ?",
            timeInterval * 1000, limit
        ) { rs: ResultSet, _: Int ->
            Product(
                rs.getLong(id),
                rs.getString(name),
                rs.getDouble(price),
                rs.getString(currency),
                rs.getString(url),
                rs.getString(xpath),
                rs.getString(priceStr),
                rs.getLong(lastCheck)
            )
        }
    }

    fun getXpathByUrl(baseUrl: String): List<String> {

        return jdbcTemplate.query(
            "select $xpath, count(id) as cnt from products where $url like ? group by $xpath order by cnt desc",
            "%$baseUrl%"
        ) { rs: ResultSet, _: Int ->
            rs.getString(xpath)
        }
    }

    companion object {
        const val products = "products"
        const val id = "id"
        const val name = "name"
        const val price = "price"
        const val currency = "currency"
        const val url = "url"
        const val xpath = "xpath"
        const val priceStr = "price_str"
        const val lastCheck = "last_check"
    }
}