package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.pcmallcompose.core.model.Brand
import java.time.LocalDateTime

@Entity(tableName = "brand", primaryKeys = ["id", "cid"])
data class BrandEntity(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "cid") val cid: Int,
    @ColumnInfo(name = "bname") val bname: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime,
    @ColumnInfo(name = "update_time") val updateTime: LocalDateTime? = null,
    @ColumnInfo(name = "is_delete") val isDelete: Int,
) {
    fun toBrand(): Brand = Brand(
        id = id,
        bname = bname,
        image = image,
        createTime = createTime,
        updateTime = updateTime,
        isDelete = isDelete,
    )

    companion object {
        @JvmStatic
        fun fromBrand(brand: Brand, cid: Int): BrandEntity = BrandEntity(
            id = brand.id,
            cid = cid,
            bname = brand.bname,
            image = brand.image,
            createTime = brand.createTime,
            updateTime = brand.updateTime,
            isDelete = brand.isDelete,
        )
    }
}