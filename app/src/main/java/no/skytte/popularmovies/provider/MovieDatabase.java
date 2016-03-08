/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.skytte.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import no.skytte.popularmovies.provider.MoviesContract.MoviesColumns;
import no.skytte.popularmovies.provider.MoviesContract.ReviewsColumns;
import no.skytte.popularmovies.provider.MoviesContract.TrailerColumns;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link MoviesProvider}.
 */
public class MovieDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int CUR_DATABASE_VERSION = 1 ;

    private final Context mContext;

    interface Tables {
        String MOVIES = "movies";
        String TRAILERS = "trailers";
        String REVIEWS = "reviews";
    }

    public MovieDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.MOVIES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MoviesColumns.MOVIE_ID + " INTEGER NOT NULL,"
                + MoviesColumns.MOVIE_OVERVIEW + " TEXT,"
                + MoviesColumns.MOVIE_POSTER + " TEXT,"
                + MoviesColumns.MOVIE_RELEASEDATE + " TEXT,"
                + MoviesColumns.MOVIE_TITLE + " TEXT,"
                + MoviesColumns.MOVIE_VOTEAVG + " TEXT,"
                + MoviesColumns.MOVIE_VOTECOUNT + " TEXT,"
                + "UNIQUE (" + MoviesColumns.MOVIE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.TRAILERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TrailerColumns.TRAILER_ID + " TEXT NOT NULL,"
                + TrailerColumns.TRAILER_KEY + " TEXT,"
                + TrailerColumns.TRAILER_NAME + " TEXT,"
                + TrailerColumns.TRAILER_SITE + " TEXT,"
                + "UNIQUE (" + TrailerColumns.TRAILER_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.REVIEWS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReviewsColumns.REVIEW_ID + " TEXT NOT NULL,"
                + ReviewsColumns.REVIEW_AUTHOR + " TEXT,"
                + ReviewsColumns.REVIEW_CONTENT + " TEXT,"
                + "UNIQUE (" + ReviewsColumns.REVIEW_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteDatabase(mContext);
        onCreate(db);
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
