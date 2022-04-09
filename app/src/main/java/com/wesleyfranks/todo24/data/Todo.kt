package com.wesleyfranks.todo24.data

import androidx.room.*
import java.sql.RowId
import java.time.LocalDateTime


@Entity(tableName = "todo_table")
data class Todo (
        @PrimaryKey(autoGenerate = true) var pk:Int = 0,
        var title: String,
        var timestamp: Long,
        var completed: Boolean = false
){

}
