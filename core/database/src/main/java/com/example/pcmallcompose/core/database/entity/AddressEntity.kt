package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.Address
import java.time.LocalDateTime

@Entity(tableName = "address")
data class AddressEntity(
    @PrimaryKey val id: Int, //地址编号
    @ColumnInfo(name = "uid") val uid: String, //用户编号
    @ColumnInfo(name = "province") val province: String, //省
    @ColumnInfo(name = "city") val city: String, //市
    @ColumnInfo(name = "district") val district: String, //区
    @ColumnInfo(name = "address_detail") val addressDetail: String, //详细地址
    @ColumnInfo(name = "receiver_name") val receiverName: String, //收件人
    @ColumnInfo(name = "phone") val phone: String, //手机号码
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime, //创建时间
    @ColumnInfo(name = "update_time") val updateTime: LocalDateTime? = null, //修改时间
    @ColumnInfo(name = "is_default") val isDefault: Int, //是否选中（0不选中 1选中）
) {
    fun toAddress(): Address = Address(
        id = id,
        uid = uid,
        province = province,
        city = city,
        district = district,
        addressDetail = addressDetail,
        receiverName = receiverName,
        phone = phone,
        createTime = createTime,
        updateTime = updateTime,
        isDefault = isDefault
    )

    companion object {
        @JvmStatic
        fun fromAddress(address: Address): AddressEntity = AddressEntity(
            id = address.id,
            uid = address.uid,
            province = address.province,
            city = address.city,
            district = address.district,
            addressDetail = address.addressDetail,
            receiverName = address.receiverName,
            phone = address.phone,
            createTime = address.createTime,
            updateTime = address.updateTime,
            isDefault = address.isDefault
        )
    }
}