package com.wesleyfranks.todo24.data

import androidx.room.*
import java.sql.RowId
import java.time.LocalDateTime


@Entity(tableName = "todo_table")
data class Todo (
        val title: String,
        val timestamp: Long,
        val completed: Boolean = false
){
        @PrimaryKey(autoGenerate = true)
        var pk:Int = 0
}
