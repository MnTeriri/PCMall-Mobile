package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.Category
import java.time.LocalDateTime

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "cname") val cname: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime,
    @ColumnInfo(name = "update_time") val updateTime: LocalDateTime? = null,
    @ColumnInfo(name = "is_delete") val isDelete: Int,
) {
    fun toCategory(): Category = Category(
        id = id,
        cname = cname,
        createTime = createTime,
        updateTime = updateTime,
        isDelete = isDelete,
    )

    companion object {
        @JvmStatic
        fun fromCategory(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            cname = category.cname,
            createTime = category.createTime,
            updateTime = category.updateTime,
            isDelete = category.isDelete,
        )
    }
}