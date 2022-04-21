package com.notiprice.scarper

import mu.KotlinLogging
import org.jsoup.Jsoup
import us.codecraft.xsoup.Xsoup

//            val apiKey = "cc4f9ce0-bcbc-11ec-94c9-1125c5e45be1"
//            val apiEndPoint = "https://app.zenscrape.com/api/v1/get" +
//                    "?apikey=$apiKey" +
//                    "&url=$urlToScrape"
private val logger = KotlinLogging.logger {}

fun getValueByXpath(url: String, xpath: String): String? =
    try {
        Xsoup.compile(xpath)
            .evaluate(Jsoup.connect(url).get())
            .elements.first()
            ?.childNodes()?.first()
            ?.outerHtml()
    } catch (th: Throwable) {
        logger.warn { "Cannot read a value by xpath" }
        null
    }