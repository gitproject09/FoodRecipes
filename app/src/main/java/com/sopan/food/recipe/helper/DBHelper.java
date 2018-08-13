package com.sopan.food.recipe.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.sopan.food.recipe.model.CategoryItem;
import com.sopan.food.recipe.model.RecipeItem;
import com.sopan.food.recipe.model.SearchResultItem;

import java.util.ArrayList;

/**
 * Created by Sopan on 2017-03-14.
 */

public class DBHelper extends SQLiteOpenHelper
{
    ImageHelper imageHelper = new ImageHelper();

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table
        db.execSQL("CREATE TABLE RECIPES( _id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT,"
                + "recipeName TEXT, author TEXT, uploardDate TEXT, howTo TEXT, description TEXT,"
                + "thumbnail BLOB, mainImg BLOB, likeCount INTEGER);");

        db.execSQL("CREATE TABLE INGREDIENTS( _id INTEGER PRIMARY KEY AUTOINCREMENT, recipeID INTEGER, ingreName TEXT);");

        db.execSQL("CREATE TABLE LIKECOUNT( _id INTEGER PRIMARY KEY AUTOINCREMENT, userID TEXT, recipeID INTEGER);");

        db.execSQL("CREATE TABLE USER( _id INTEGER PRIMARY KEY AUTOINCREMENT, userID TEXT, password TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public  void  user_Insert(String userid, String password)
    {
        // open read and write database
        SQLiteDatabase db = getWritableDatabase();
        // execute insert query
        db.execSQL("INSERT INTO USER VALUES(null, '" + userid + "', '" + password + "');");

        db.close();
    }

    //registration check function
    public boolean user_IsUsernameFree(String userId)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USER WHERE userID = '" + userId + "'", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                return false;
            }
        }
        cursor.close();
        db.close();
        return true;
    }

    //login function
    public boolean user_Login(String userId, String password)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USER WHERE userID = '" + userId + "' AND password = '"+password+"'", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                return true;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    public int user_Allcount()
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM USER", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        }
        cursor.close();
        db.close();

        return count;
    }

    public void recipes_Insert(String category, String recipeName, String author,
                               String uploardDate, String howTo, String description,
                               byte[] thumbnail, byte[] mainImg, int likeCount) {

        // open read and write database
        SQLiteDatabase db = getWritableDatabase();
        // execute insert query
        //db.execSQL("INSERT INTO RECIPES VALUES(null, '" + category + "', '" + recipeName + "', " + thumbnail  + ");");
        SQLiteStatement p = db.compileStatement("INSERT INTO recipes values(?,?,?,?,?,?,?,?,?,?);");
        p.bindNull(1);
        p.bindString(2, category);
        p.bindString(3, recipeName);
        p.bindString(4, author);
        p.bindString(5, uploardDate);
        p.bindString(6, howTo);
        p.bindString(7, description);
        p.bindBlob(8, thumbnail);
        p.bindBlob(9, mainImg);
        p.bindLong(10, likeCount);
        p.execute();
        db.close();
    }

    public  void  ingredients_Insert(int recipeid, String ingreName)
    {
        // open read and write database
        SQLiteDatabase db = getWritableDatabase();
        // execute insert query
        db.execSQL("INSERT INTO INGREDIENTS VALUES(null, " + recipeid + ", '" + ingreName + "');");

        db.close();
    }

    public ArrayList<String> ingredients_SelectByRecipeId(int id)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> ingredients = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT ingreName FROM INGREDIENTS WHERE recipeID = " + id, null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                ingredients.add(cursor.getString(0));
            }
        }
        cursor.close();
        db.close();

        return ingredients;


    }

    //Miju
    public ArrayList<SearchResultItem> ingredients_selectRecipeByIngredientName(ArrayList<String> ingredientsName){
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<SearchResultItem> idRecipes = new ArrayList<>();
        String strNames = "";
        for (int i=0; i < ingredientsName.size(); i++)
        {
            strNames += "ingreName = '" + ingredientsName.get(i) + "'";
            if (i != ingredientsName.size()-1)
            {
                strNames += " OR ";
            }
        }

        Cursor cursor = db.rawQuery("SELECT recipeID, count(*) FROM INGREDIENTS WHERE "+
                strNames + " GROUP BY recipeID", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                idRecipes.add(new SearchResultItem(cursor.getInt(0), cursor.getInt(1)));
            }
        }
        cursor.close();
        db.close();
        return idRecipes;

    }

    //return the id of the recipes based on ingredientName TO FIX!
    public ArrayList<Integer> ingredients_selectIdRecipeByIngredientName(ArrayList<String> ingredientsName){
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Integer> idRecipes = new ArrayList<>();
        // query the db findind the recipeID through the
        for(int i =0; i<ingredientsName.size();i++) {
            Cursor cursor = db.rawQuery("SELECT recipeID FROM INGREDIENTS WHERE ingreName = '" + ingredientsName.get(1) + "'", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    idRecipes.add(cursor.getInt(0));
                }
            }
            cursor.close();
        }

        db.close();
        return idRecipes;

    }


    /*
    return the number of ingredients of a recipe based on idRecipe TO FIX!
     */
    public int countIngredientsPerRecipes(int idRecipes){
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM INGREDIENTS WHERE recipeID= " + idRecipes, null );
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                count++;
            }
        }
        cursor.close();
        db.close();

        return count;
    }



    public int recipes_GetIdByName(String name)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        //variable id
        int id= -1;
        Cursor cursor = db.rawQuery("SELECT _id FROM RECIPES WHERE recipeName = '" + name + "' ORDER BY _id DESC LIMIT 1", null);
        if(cursor != null)
        {
            while (cursor.moveToNext()){
                id = cursor.getInt(0);
            }
        }

        cursor.close();
        db.close();
        return id;
    }

    public ArrayList<RecipeItem> recipes_SelectNew()
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecipeItem> allRecipes = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES ORDER BY _id DESC LIMIT 3", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                allRecipes.add(new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                ));
            }
        }
        cursor.close();
        db.close();

        return allRecipes;
    }

    public ArrayList<CategoryItem> recipes_SelectCategory()
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<CategoryItem> categoryList = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT category, mainImg FROM RECIPES GROUP BY category HAVING max(_id)", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                categoryList.add(new CategoryItem(
                        cursor.getString(0),
                        cursor.getBlob(1)
                ));
            }
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    public ArrayList<RecipeItem> recipes_SelectBest()
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecipeItem> allRecipes = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES ORDER BY likeCount DESC LIMIT 3", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                allRecipes.add(new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                ));
            }
        }
        cursor.close();
        db.close();

        return allRecipes;
    }

    public ArrayList<RecipeItem> recipes_SelectAll()
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecipeItem> allRecipes = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                allRecipes.add(new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                ));
            }
        }
        cursor.close();
        db.close();

        return allRecipes;
    }

    public ArrayList<RecipeItem> recipes_SelectByCategory(String category)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecipeItem> allRecipes = new ArrayList<>();
        // Get all recipes data
        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES WHERE category = '" + category + "'", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                allRecipes.add(new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                ));
            }
        }
        cursor.close();
        db.close();

        return allRecipes;
    }

    public RecipeItem recipes_SelectByName(String name)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES WHERE recipeName = '" + name + "'", null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                RecipeItem recipe = new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                );
                cursor.close();
                db.close();
                return recipe;
            }


        }
        return  null;
    }

    public RecipeItem recipes_SelectById(int id)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RECIPES WHERE _id = " + id, null);
        if (cursor != null)
        {
            while (cursor.moveToNext()) {
                RecipeItem recipe = new RecipeItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getBlob(7),
                        cursor.getBlob(8),
                        cursor.getInt(9)
                );
                cursor.close();
                db.close();
                return recipe;
            }


        }
        return  null;
    }

    public int recipes_AddLike(String userId, int recipeId)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        int count= -1;
        Cursor cursor = db.rawQuery("SELECT likeCount FROM RECIPES WHERE _id = " + recipeId , null);
        if(cursor != null)
        {
            while (cursor.moveToNext()){
                count = cursor.getInt(0);
            }
        }
        cursor.close();

        db = getWritableDatabase();
        db.execSQL("UPDATE RECIPES SET likeCount = " + (count +1) + " WHERE _id = " + recipeId + ";");
        db.execSQL("INSERT INTO LIKECOUNT values(null, '" + userId + "', " + recipeId + ");");
        db.close();
        return count;
    }

    public int recipes_MinusLike(String userId, int recipeId)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        int count= -1;
        Cursor cursor = db.rawQuery("SELECT likeCount FROM RECIPES WHERE _id = " + recipeId , null);
        if(cursor != null)
        {
            while (cursor.moveToNext()){
                count = cursor.getInt(0);
            }
        }
        cursor.close();

        db = getWritableDatabase();
        db.execSQL("UPDATE RECIPES SET likeCount = " + (count - 1) + " WHERE _id = " + recipeId + ";");
        db.execSQL("DELETE FROM LIKECOUNT WHERE userID = '" + userId + "' AND recipeID = " + recipeId + ";");
        db.close();
        return count;
    }

    public boolean like_GetLikeYNByUserId(String userId, int recipeId)
    {
        // Open available reading database
        SQLiteDatabase db = getReadableDatabase();
        //variable id
        boolean likeNY= false;
        Cursor cursor = db.rawQuery("SELECT * FROM LIKECOUNT WHERE userID = '" + userId + "' AND recipeID = " + recipeId, null);
        if(cursor != null)
        {
            while (cursor.moveToNext()){
                likeNY = true;
            }
        }
        cursor.close();
        db.close();
        return likeNY;
    }

}