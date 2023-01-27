package parser.html

import org.jsoup.Jsoup

fun fileParser (url: String, semestrType: Boolean): List<String> {
    val temp = Jsoup.connect(url)
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .referrer("https://www.google.com")
        .get()
        .getElementsByClass("docs pane")
        .toList()
        .filter {
            (it.text().regionMatches(33, "осенний семестр", 0, 15) && it.text()
                .regionMatches(11, "очной", 0, 5) && semestrType)
                    || (it.text().regionMatches(33, "весенний семестр", 0, 16) && it.text()
                .regionMatches(11, "очной", 0, 5) && !semestrType)
        }
        return if(temp.isNotEmpty()) temp.map { it.getElementsByTag("a") }.first().map { "https://cchgeu.ru" + it.attr("href") }
        else listOf<String>()
}