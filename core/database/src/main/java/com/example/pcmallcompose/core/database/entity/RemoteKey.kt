package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "remote_key", primaryKeys = ["table_name", "label"])
data class RemoteKey(
    @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "current_page") val currentPage: Int
)