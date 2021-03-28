package com.wesleyfranks.todo24.data

import androidx.room.*
import java.sql.RowId



@Entity(tableName = "todo_database")
data class Todo (
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "timestamp") var timestamp: String
){
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "primaryKey") var pk:Int = 0
}
