package com.example.kutsinakasama

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_EMAIL TEXT,
                $COL_PASSWORD TEXT,
                $COL_IMAGE_URI TEXT
            );
        """.trimIndent()

        db.execSQL(createUserTable)


        val insertDummy = """
            INSERT INTO $TABLE_USERS ($COL_NAME, $COL_EMAIL, $COL_PASSWORD, $COL_IMAGE_URI)
            VALUES ('John Doe', 'johndoe@example.com', '123456', NULL);
        """.trimIndent()

        db.execSQL(insertDummy)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun getUserById(id: Int): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COL_ID = ?",
            arrayOf(id.toString())
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    companion object {
        private const val DB_NAME = "kusinakasama.db"
        private const val DB_VERSION = 1

        const val TABLE_USERS = "users"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_IMAGE_URI = "imageUri"
    }
}
