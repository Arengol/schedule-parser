package parser.doc

import data.StudClassData
import org.apache.poi.hwpf.HWPFDocument
import java.net.URL


fun docParser(url: URL): List<StudClassData> {
    println(url)
    val classData = arrayListOf<StudClassData>()
    val range = HWPFDocument(url.openStream()).range // если будет течь сохрани поток отдельно и закрой его потом
    val groupName = range.getParagraph(0).text().split(" ").last().replace("\r", "")
    val table = range.getTable(range.getParagraph(1))
    val timeRow = table.getRow(1)

    for (rowInd in 2 until table.numRows()) {
        val scheduleRow = table.getRow(rowInd)

        for (colInd in 2 until scheduleRow.numCells()) {
            var rawData = ""
            val scheduleCell = scheduleRow.getCell(colInd)

            for (parInd in 0 until scheduleCell.numParagraphs()) {
                var temp = scheduleCell.getParagraph(parInd).text()
                if (temp.endsWith("\r")) temp = temp.substring(0, temp.length - 1)
                rawData += temp
            }
           // val debug = rawData
            rawData = rawData.replace("\u0007", "")
                .replace("""-\s+|\s+-|_""".toRegex(), " ")
                .replace("""\Sа\.""".toRegex(), " а.")
                .replace("""1\s*п/г""".toRegex(), " 1п/г ")
                .replace("""2\s*п/г""".toRegex(), " 2п/г ")
                .replace("""АСС\.""".toRegex(), " АСС. ")
                .replace("""СТ\.ПР\.""".toRegex(), " СТ.ПР. ")
                .replace("""ПРОФ\.""".toRegex(), " ПРОФ. ")
                .replace("""ДОЦ\.""".toRegex(), " ДОЦ. ")
                .replace("""[^.]ПР\.""".toRegex(), " ПР. ")
                .replace("""лек\.""".toRegex(), " лек. ")
                .replace("""лаб\.""".toRegex(), " лаб. ")
                .replace("""пр\.""".toRegex(), " пр. ")
                .replace("""[\s]{2,}""".toRegex(), " ")
                .trim()
            if (rawData.length<3) continue
           //if(!(rawData.startsWith("лек") || rawData.startsWith("лаб") || rawData.startsWith("пр")))
            //  println(rawData)
            val pull = rawData.split(" ")

            var studClassName = ""
            var studClassType = ""
            val auditory = mutableListOf<String>()
            val mentor = mutableListOf<String>()

            var findClass = true
            var findMentor = false
            var findAuditory = false
            var newClassAlarm = false
            var tempMentor = ""

            for (element in pull){

                if (element == "АСС." || element == "СТ.ПР." || element == "ПРОФ." || element == "ДОЦ." || element == "ПР.") {
                    findClass = false
                    findMentor = true
                    findAuditory = false
                    if (tempMentor.length>0){
                        mentor.add(tempMentor.replace("""1п/г|2п/г""".toRegex(), "").trim())
                        tempMentor = ""
                    }
                }

                if (element.startsWith("а.")) {
                    findClass = false
                    findMentor = false
                    findAuditory = true
                }

                if (findClass && !newClassAlarm) {
                    studClassName += "$element "
                }

                if (findClass && newClassAlarm) {
                    classData.add(
                        StudClassData(
                            range.getParagraph(0).text().split(" ").last().replace("\r", ""),
                            timeRow.getCell(colInd).text().replace("\u0007", "").trim(),
                            scheduleRow.getCell(0).text().replace("\u0007", "").trim(),
                            if (rowInd<8) 0 else 1,
                            studClassName.trim(),
                            "",
                            auditory.toList(),
                            mentor.toList()
                        )
                    )
                    studClassName = ""
                    auditory.clear()
                    mentor.clear()
                    studClassName += "$element "
                    newClassAlarm = false
                }

                if (findMentor) {
                    tempMentor += "$element "
                }
                if (findAuditory) {
                    if (tempMentor.isNotEmpty()) {
                        mentor.add(tempMentor.replace("""1п/г|2п/г""".toRegex(), "").trim())
                        tempMentor = ""
                    }
                    auditory.add(element.replace("а.", ""))
                    findClass = true
                    findMentor = false
                    findAuditory = false
                    newClassAlarm = true
                }

            }

            classData.add(
                StudClassData(
                    range.getParagraph(0).text().split(" ").last().replace("\r", ""),
                    timeRow.getCell(colInd).text().replace("\u0007", "").trim(),
                    scheduleRow.getCell(0).text().replace("\u0007", "").trim(),
                    if (rowInd<8) 0 else 1,
                    studClassName.trim(),
                    "",
                    auditory.toList(),
                    mentor.toList()
                )
            )

            studClassName = ""
            auditory.clear()
            mentor.clear()
        }
    }
    return classData
}

//fun parseClass(rawData: String): List<StudClassData>{
//    val classData = arrayListOf<StudClassData>()
//    val pull = rawData.split(" ")
//
//    var studClassName = ""
//    var studClassType = ""
//    val auditory = mutableListOf<String>()
//    val mentor = mutableListOf<String>()
//
//    var findClass = true
//    var findMentor = false
//    var findAuditory = false
//    var newClassAlarm = false
//    var tempMentor = ""
//
//    for (element in pull){
//
//        if (element == "АСС." || element == "СТ.ПР." || element == "ПРОФ." || element == "ДОЦ." || element == "ПР.") {
//            findClass = false
//            findMentor = true
//            findAuditory = false
//            if (tempMentor.length>0){
//                mentor.add(tempMentor)
//                tempMentor = ""
//            }
//        }
//
//        if (element.startsWith("а.")) {
//            findClass = false
//            findMentor = false
//            findAuditory = true
//        }
//
//        if (findClass && !newClassAlarm) {
//            studClassName += "$element "
//        }
//
//        if (findClass && newClassAlarm) {
//            classData.add(
//                StudClassData(
//                    "",
//                    "",
//                    "",
//                    0,
//                    studClassName.trim(),
//                    "",
//                    auditory.toList(),
//                    mentor.toList()
//                )
//            )
//
//            studClassName = ""
//            auditory.clear()
//            mentor.clear()
//            studClassName += "$element "
//            newClassAlarm = false
//        }
//
//        if (findMentor) {
//            tempMentor += "$element "
//        }
//        if (findAuditory) {
//            if (tempMentor.isNotEmpty()) {
//                mentor.add(tempMentor.trim())
//                tempMentor = ""
//            }
//            auditory.add(element.replace("а.", ""))
//            findClass = true
//            findMentor = false
//            findAuditory = false
//            newClassAlarm = true
//        }
//
//    }
//
//    classData.add(
//        StudClassData(
//            "",
//            "",
//            "",
//            0,
//            studClassName.trim(),
//            "",
//            auditory.toList(),
//            mentor.toList()
//        )
//    )
//    return classData
//}