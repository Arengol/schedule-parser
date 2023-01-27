package data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time

object Accounts : Table("accounts") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 20)
    val passwordHash = varchar("password_hash", 32)
    val status = integer("status")
    val token = varchar("token", 50)
    override val primaryKey = PrimaryKey(id, name = "accounts_pk")
}

object StudentGroup : Table("student_group") {
    val name = varchar( "name",10)
    override val primaryKey = PrimaryKey(name, name = "group_pk")
}

object Student : Table("student") {
    val groupName = varchar("student_group_name", 10).references(StudentGroup.name)
    val account = integer("account_id").references(Accounts.id)
}

object TimeSchedule : Table("time_schedule") {
    val id = integer("id").autoIncrement()
    val time = varchar("time", 15)
    val dayOfWeek = varchar("day_week", 11)
    val weekType = integer("week_type")
    override val primaryKey = PrimaryKey(id, name = "time_schedule_pk")
}

object Auditory : Table("auditory") {
    val name = varchar("name", 10)
    override val primaryKey = PrimaryKey(name, name = "auditory_pk")
}

object Mentor : Table("mentor") {
    val name = varchar("name", 40)
    val account = integer("account_id").references(Accounts.id)
    override val primaryKey = PrimaryKey(name, name = "mentor_pk")
}

object Event : Table("event") {
    val timeBegin = time("time_begin")
    val id = integer("id").autoIncrement()
    val timeEnd = time("time_end")
    val date = date("date")
    val name = varchar("name", 50)
    val account = integer("account_id").references(Accounts.id)
    override val primaryKey = PrimaryKey(id, name = "event_pk")
}

object StudClass : Table("class") {
    val name = varchar("name", 250)
    val type = varchar("type",8)
    val timeSchedule = integer("time_schedule_id").references(TimeSchedule.id)
    val id = integer("id").autoIncrement()
    val accountId = integer("account_id").references(Accounts.id)
    override val primaryKey = PrimaryKey(id, name = "class_pk")
}

object ClassAndAuditory : Table("class_and_auditory") {
    val classId = integer("class_id").references(StudClass.id)
    val auditory = varchar("auditory_name", 10).references(Auditory.name)
}

object ClassAndGroup : Table("class_and_group") {
    val classId = integer("class_id").references(StudClass.id)
    val group = varchar("student_group_name", 10).references(StudentGroup.name)
}
object ClassAndMentor : Table("class_and_mentor") {
    val classId = integer("class_id").references(StudClass.id)
    val mentor = varchar("mentor_name", 40).references(Mentor.name)
}
