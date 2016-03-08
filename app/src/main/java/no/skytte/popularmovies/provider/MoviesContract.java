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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link MoviesProvider}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public final class MoviesContract {

    public static final String CONTENT_TYPE_APP_BASE = "coolmovies.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    interface MoviesColumns {
        String MOVIE_ID = "movie_id";
        String MOVIE_TITLE = "movie_title";
        String MOVIE_OVERVIEW = "movie_overview";
        String MOVIE_POSTER = "movie_posterPath";
        String MOVIE_RELEASEDATE = "movie_releaseDate";
        String MOVIE_VOTEAVG = "movie_voteAverage";
        String MOVIE_VOTECOUNT = "movie_voteCount";
    }

    interface ReviewsColumns {

        String REVIEW_ID = "review_id";
        String REVIEW_AUTHOR = "review_author";
        String REVIEW_CONTENT = "review_content";
    }

    interface TrailerColumns {

        String TRAILER_ID = "trailer_id";
        String TRAILER_KEY = "trailer_key";
        String TRAILER_NAME = "trailer_name";
        String TRAILER_SITE = "trailer_site";
    }

    public static final String CONTENT_AUTHORITY = "no.skytte.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_MOVIES = "movies";

    private static final String PATH_REVIEWS = "reviews";

    private static final String PATH_TRAILERS = "trailers";

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class Movies implements MoviesColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE_ID = "movie";

        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Reviews implements ReviewsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE_ID = "review";

        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Trailers implements TrailerColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE_ID = "review";

        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private MoviesContract() {
    }
}
