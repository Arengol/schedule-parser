package data

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class StudClassData (
    val group: String,
    val time: String,
    val dayWeek: String,
    val weekType: Int,
    val name: String,
    val classType: String,
    val auditory: List<String>,
    val mentor: List<String>
)

fun insertData (studClassData: StudClassData) {
    var groupID: String? = ""
    var timeID: Int? = 0
    var classId = 0
    transaction {
         groupID =
            StudentGroup.select { StudentGroup.name eq studClassData.group }.limit(1).firstOrNull()
                ?.get(StudentGroup.name)
        if (groupID == null) {
            groupID = StudentGroup.insert {
                it[name] = studClassData.group
            }.resultedValues!!.first()[StudentGroup.name]
        }
    }
    transaction {
            timeID =
                TimeSchedule.select { TimeSchedule.time eq studClassData.time and (TimeSchedule.dayOfWeek eq studClassData.dayWeek) and (TimeSchedule.weekType eq studClassData.weekType)}.limit(1).firstOrNull()?.get(TimeSchedule.id)
        if (timeID == null) {
            timeID = TimeSchedule.insert {
                it[time] = studClassData.time
                it[dayOfWeek] = studClassData.dayWeek
                it[weekType] = studClassData.weekType
            }.resultedValues!!.first()[TimeSchedule.id]
        }
    }

     transaction {
         classId = StudClass.insert {
            it[name] = studClassData.name
            it[type] = studClassData.classType
            it[timeSchedule] = timeID!!
        }.resultedValues!!.first()[StudClass.id]

        ClassAndGroup.insert {
            it[ClassAndGroup.classId] = classId
            it[ClassAndGroup.group] = groupID!!
        }
    }

    transaction {
        for (mentorData in studClassData.mentor) {
            var mentorID = Mentor.select { Mentor.name eq mentorData }.limit(1).firstOrNull()?.get(Mentor.name)
            if (mentorID == null) {
                if (mentorData.length > 25) {
                    val debug = mentorData.length
                    println("error")
                }
                mentorID = Mentor.insert {
                    it[Mentor.name] = mentorData
                }.resultedValues!!.first()[Mentor.name]
            }
            ClassAndMentor.insert {
                it[ClassAndMentor.classId] = classId
                it[ClassAndMentor.mentor] = mentorID
            }
        }
    }

    transaction {
        for (auditoryData in studClassData.auditory) {
            var auditoryID =
                Auditory.select { Auditory.name eq auditoryData }.limit(1).firstOrNull()?.get(Auditory.name)
            if (auditoryID == null) {
                auditoryID = Auditory.insert {
                    it[name] = auditoryData
                }.resultedValues!!.first()[Auditory.name]
            }
            ClassAndAuditory.insert {
                it[ClassAndAuditory.classId] = classId
                it[ClassAndAuditory.auditory] = auditoryID
            }
        }
    }

}

fun clearData () {
    transaction {
        ClassAndGroup.deleteAll()
        ClassAndAuditory.deleteAll()
        ClassAndMentor.deleteAll()
        StudClass.deleteAll()
        TimeSchedule.deleteAll()
    }
}