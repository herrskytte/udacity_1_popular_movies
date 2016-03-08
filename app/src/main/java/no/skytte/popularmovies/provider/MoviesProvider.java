/*
 * Copyright 2015 Google Inc. All rights reserved.
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

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import no.skytte.popularmovies.provider.MovieDatabase.Tables;
import no.skytte.popularmovies.provider.MoviesContract.Movies;
import no.skytte.popularmovies.provider.MoviesContract.Reviews;
import no.skytte.popularmovies.provider.MoviesContract.Trailers;

public class MoviesProvider extends ContentProvider {

    private MovieDatabase mOpenHelper;

    private MoviesProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDatabase(getContext());
        mUriMatcher = new MoviesProviderUriMatcher();
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        MovieDatabase.deleteDatabase(context);
        mOpenHelper = new MovieDatabase(getContext());
    }

    @Override
    public String getType(@NonNull Uri uri) {
        MoviesUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        return matchingUriEnum.contentType;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        MoviesUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        final SelectionBuilder builder = buildSelection(uri, matchingUriEnum.code);

        Cursor cursor = builder
                .where(selection, selectionArgs)
                .query(db, projection, sortOrder);

        Context context = getContext();
        if (null != context) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        MoviesUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        if (matchingUriEnum.table != null) {
            db.insertOrThrow(matchingUriEnum.table, null, values);
            notifyChange(uri);
        }

        switch (matchingUriEnum) {
            case MOVIES: {
                return Movies.buildUri(values.getAsString(Movies.MOVIE_ID));
            }
            case TRAILERS: {
                return Trailers.buildUri(values.getAsString(Trailers.TRAILER_ID));
            }
            case REVIEWS: {
                return Reviews.buildUri(values.getAsString(Reviews.REVIEW_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No updates in this app: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (uri == MoviesContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        MoviesUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        final SelectionBuilder builder = buildSelection(uri, matchingUriEnum.code);

        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    /**
     * Notifies the system that the given {@code uri} data has changed.
     */
    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private SelectionBuilder buildSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        MoviesUriEnum matchingUriEnum = mUriMatcher.matchCode(match);
        if (matchingUriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (matchingUriEnum) {
            case MOVIES: {
                return builder.table(Tables.MOVIES);
            }
            case MOVIES_ID: {
                final String id = Movies.getId(uri);
                return builder.table(Tables.MOVIES)
                        .where(Movies.MOVIE_ID + "=?", id);
            }
            case TRAILERS: {
                return builder.table(Tables.TRAILERS);
            }
            case TRAILERS_ID: {
                final String id = Trailers.getId(uri);
                return builder.table(Tables.TRAILERS)
                        .where(Trailers.TRAILER_ID + "=?", id);
            }
            case REVIEWS: {
                return builder.table(Tables.REVIEWS);
            }
            case REVIEWS_ID: {
                final String id = Reviews.getId(uri);
                return builder.table(Tables.REVIEWS)
                        .where(Reviews.REVIEW_ID + "=?", id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
