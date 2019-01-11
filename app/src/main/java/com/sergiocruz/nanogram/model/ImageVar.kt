package com.sergiocruz.nanogram.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sergiocruz.nanogram.model.endpoint.usermedia.Caption
import com.sergiocruz.nanogram.model.endpoint.usermedia.Comments
import com.sergiocruz.nanogram.model.endpoint.usermedia.Images
import com.sergiocruz.nanogram.model.endpoint.usermedia.Likes

@Entity(tableName = "instaposts")
data class ImageVar @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "columnId")
    val id: Int?,
    val images: Images?,
    val likes: Likes? = null,
    val comments: Comments? = null,
    val caption: Caption? = null
)