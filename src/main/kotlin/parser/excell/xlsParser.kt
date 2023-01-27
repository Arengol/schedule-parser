package parser.excell

import data.StudClassData
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.sl.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.net.URL
import java.util.*
import java.util.Arrays.equals

//https://cchgeu.ru/upload/iblock/c39/2aclmkf720k5qd4hgfb4iir5xdyk24yf/FEMIT-pBI_191_-EBR_181_-EBR_182.xls



fun excellParser(url:URL): List<StudClassData> {
    val classData = arrayListOf<StudClassData>()
    val workbook = if(url.toString().endsWith("xlsx"))
        XSSFWorkbook(url.openStream())
    else HSSFWorkbook(url.openStream())
    var startRow: Int = 0
    for (sheet in workbook) {
        for (i in 0 .. 15){
            if (sheet.getRow(i)?.getCell(0)?.stringCellValue?.lowercase().equals("день") ||
                sheet.getRow(i)?.getCell(0)?.stringCellValue?.lowercase().equals("дата")) {
                startRow = i
                break
            }
        }
        println("$startRow ${sheet.getRow(startRow)?.getCell(2)?.stringCellValue}")
    }

    return  classData
}

fun dayOfWeekConverter (string: String): String =
    when (string.uppercase(Locale.getDefault())) {
        "ВОСКРЕСЕНЬЕ" ->  "Вск"
        "ПОНЕДЕЛЬНИК" ->  "Пнд"
        "ВТОРНИК" ->  "Втр"
        "СРЕДА" ->  "Срд"
        "ЧЕТВЕРГ" ->  "Чтв"
        "ПЯТНИЦА" ->  "Птн"
        "СУББОТА" ->  "Сбт"
        else -> ""
    }

