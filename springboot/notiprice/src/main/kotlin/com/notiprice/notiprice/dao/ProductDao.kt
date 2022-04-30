package com.notiprice.notiprice.dao

import com.notiprice.notiprice.entity.Product
import org.springframework.jdbc.core.JdbcTemplate
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
                    "insert into $products ($name, $price, $currency, $url, $xpath, $priceStr) values (?, ?, ?, ?, ?,?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            ps.setString(1, product.name)
            ps.setString(2, product.price.toString())
            ps.setString(3, product.currency)
            ps.setString(4, product.url)
            ps.setString(5, product.xpath)
            ps.setString(6, product.priceStr)
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
                    "$xpath = ? " +
                    "$priceStr = ? " +
                    "where $id = ?",
            product.name, product.price, product.currency, product.url, product.xpath, product.priceStr, product.id
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


    @Deprecated("Only for developing")
    fun findAll(): List<Product> = jdbcTemplate.query(
        "select * from $products"
    ) { rs: ResultSet, _: Int ->
        Product(
            rs.getLong(id),
            rs.getString(name),
            rs.getDouble(price),
            rs.getString(currency),
            rs.getString(url),
            rs.getString(xpath),
            rs.getString(priceStr),
        )
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
    }
}