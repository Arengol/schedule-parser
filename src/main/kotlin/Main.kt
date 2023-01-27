import data.*
import org.jetbrains.exposed.sql.*
import org.jsoup.Jsoup
import parser.doc.docParser
import parser.excell.excellParser
//import parser.doc.parseClass
import parser.html.facultyParser
import parser.html.fileParser
import java.net.URL

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/schedule", driver = "org.postgresql.Driver",
        user = "admin", password = "h9Zhugku4CtxQksWaJ3BmpLF")
        println("cleaning")
        clearData()
    val document = arrayListOf<String>()
       url.forEach {
           document.addAll(fileParser(it, false))
       }
    var counter = 0
    val schedule = mutableListOf<StudClassData>()
    println("parsing")
    document.filter { it.endsWith("doc") }.forEach { schedule.addAll(docParser(URL(it)))
    counter++
    }
    println("uploading")
    schedule.forEach { insertData(it) }
    println(counter)
}

val url = listOf<String>(
    "https://cchgeu.ru/studentu/schedule/asp/",
    "https://cchgeu.ru/studentu/schedule/dtf/",
    "https://cchgeu.ru/studentu/schedule/stf/",
    "https://cchgeu.ru/studentu/schedule/fm/",
    "https://cchgeu.ru/studentu/schedule/poi/",
    "https://cchgeu.ru/studentu/schedule/spo/",
    "https://cchgeu.ru/studentu/schedule/sf/",
    "https://cchgeu.ru/studentu/schedule/fag/",
    "https://cchgeu.ru/studentu/schedule/fisis/",
    "https://cchgeu.ru/studentu/schedule/fitkb/",
    "https://cchgeu.ru/studentu/schedule/raspisanie-fakultet-mashinostroeniya-i-aerokosmicheskoy-tekhniki/",
    "https://cchgeu.ru/studentu/schedule/fre/",
    "https://cchgeu.ru/studentu/schedule/femit/",
    "https://cchgeu.ru/studentu/schedule/fesu/"

)