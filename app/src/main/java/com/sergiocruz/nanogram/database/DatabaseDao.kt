package com.sergiocruz.nanogram.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.sergiocruz.nanogram.model.ImageVar

@Dao
interface DatabaseDao {

    @get:Query("SELECT * FROM instaposts")
    val getAllPosts: MutableList<ImageVar>

    @get:Query("SELECT COUNT(*) FROM instaposts")
    val numberOfPosts: Int?

    @Query("select * from instaposts where columnId = :id")
    fun getPostByColumnId(id: Int?): ImageVar

    @Query("DELETE FROM instaposts WHERE columnId = :columnId")
    fun deletePostByColumnId(columnId: Int?)

    @Query("DELETE FROM instaposts")
    fun deleteTable()

    @Insert(onConflict = REPLACE)
    fun saveAll(list:MutableList<ImageVar>)

    @Transaction
    fun clearAndInsert(list: MutableList<ImageVar>){
        deleteTable()
        saveAll(list)
    }


}