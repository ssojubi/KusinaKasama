package com.example.kutsinakasama

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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

        val defaultImageUri = "android.resource://com.example.kutsinakasama/${R.drawable.ic_user_icon_placeholder}"

        val insertDummy = """
        INSERT INTO $TABLE_USERS ($COL_NAME, $COL_EMAIL, $COL_PASSWORD, $COL_IMAGE_URI)
         VALUES (?, ?, ?, ?)
        """

        db.execSQL(insertDummy, arrayOf("John Doe", "johndoe@example.com", "123456", defaultImageUri))

        val createFavoritesTable = """
        CREATE TABLE favorites (
            id INTEGER PRIMARY KEY,
            title TEXT
        );
    """.trimIndent()

        db.execSQL(createFavoritesTable)
        
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS favorites")
        onCreate(db)
    }

    fun addFavorite(id: Int, title: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("title", title)
        }
        db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun removeFavorite(id: Int) {
        val db = writableDatabase
        db.delete("favorites", "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun isFavorite(id: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM favorites WHERE id=?", arrayOf(id.toString()))

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllFavorites(): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, title FROM favorites", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                list.add(Pair(id, title))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
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
        return user
    }

    fun getUserIdByEmail(email: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID FROM $TABLE_USERS WHERE $COL_EMAIL = ?",
            arrayOf(email)
        )

        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        }

        cursor.close()
        Log.d("DBHelper", "getUserIdByEmail($email) returned: $userId")
        return userId
    }


    fun insertUser(name: String, email: String, password: String): Long {
        val db = writableDatabase

        val defaultImageUri = "android.resource://com.example.kutsinakasama/${R.drawable.baseline_account_circle_24}"

        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_IMAGE_URI, defaultImageUri)
        }

        val id = db.insert(TABLE_USERS, null, values)
        return id
    }

    //if user exists in the database this function will return TRUE
    fun readUser(email: String, password: String) : Boolean{
        val db = readableDatabase
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID, COL_NAME, COL_EMAIL, COL_PASSWORD, COL_IMAGE_URI),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        return cursor.use{ it.count > 0}
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val selection = "$COL_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )


        return cursor.use { it.count > 0}
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COL_EMAIL = ?",
            arrayOf(email)
        )
        return cursor.use { c ->
            if (c.moveToFirst()) {
                User(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                    c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)),
                    c.getString(c.getColumnIndexOrThrow(COL_IMAGE_URI))
                )
            } else {
                null
            }
        }
    }



    companion object {
        private const val DB_NAME = "kusinakasama.db"
        private const val DB_VERSION = 2
        const val TABLE_USERS = "users"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_IMAGE_URI = "imageUri"
    }
}
