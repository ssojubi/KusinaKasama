package com.example.kutsinakasama

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.text.HtmlCompat
import java.io.File

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
                id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                title TEXT,
                image TEXT,
                instructions TEXT,
                ingredients TEXT,
                PRIMARY KEY (id, user_id),
                FOREIGN KEY(user_id) REFERENCES users(id)
            );
        """.trimIndent()

        db.execSQL(createFavoritesTable)
        
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS favorites")
        onCreate(db)
    }

    fun addFavorite(id: Int, userId: Int, title: String, imagePath: String?, instructions: String?, ingredients: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("user_id", userId)
            put("title", title)
            put("image", imagePath)
            put("instructions", instructions)
            put("ingredients", ingredients)
        }
        db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun removeFavorite(id: Int, userId: Int) {
        writableDatabase.delete("favorites", "id=? AND user_id=?", arrayOf(id.toString(), userId.toString()))
    }

    fun isFavorite(id: Int, userId: Int): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT id FROM favorites WHERE id=? AND user_id=?",
            arrayOf(id.toString(), userId.toString())
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getOfflineRecipe(id: Int, userId: Int): OfflineRecipe? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM favorites WHERE id=? AND user_id=?",
            arrayOf(id.toString(), userId.toString())
        )

        var r: OfflineRecipe? = null
        if (cursor.moveToFirst()) {
            r = OfflineRecipe(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getString(cursor.getColumnIndexOrThrow("instructions")),
                cursor.getString(cursor.getColumnIndexOrThrow("ingredients"))
            )
        }
        cursor.close()
        return r
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
        return userId
    }


    fun insertUser(name: String, email: String, password: String): Long {
        val db = writableDatabase
        val defaultImageUri =
            "android.resource://com.example.kutsinakasama/${R.drawable.baseline_account_circle_24}"

        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_IMAGE_URI, defaultImageUri)
        }

        return db.insert(TABLE_USERS, null, values)
    }

    //if user exists in the database this function will return TRUE
    fun readUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            selection,
            arrayOf(email, password),
            null,
            null,
            null
        )
        return cursor.use { it.count > 0 }
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            "$COL_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        return cursor.use { it.count > 0 }
    }

    fun saveImageLocally(context: Context, imageUrl: String, recipeId: Int): String? {
        return try {
            val input = java.net.URL(imageUrl).openStream()
            val file = java.io.File(context.filesDir, "recipe_image_$recipeId.jpg")
            val output = file.outputStream()

            input.copyTo(output)
            input.close()
            output.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
        private const val DB_VERSION = 3
        const val TABLE_USERS = "users"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_IMAGE_URI = "imageUri"
    }

    data class OfflineRecipe(
        val id: Int,
        val title: String,
        val image: String?,
        val instructions: String?,
        val ingredients: String?
    )
}
