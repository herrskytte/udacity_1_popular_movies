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

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Provides methods to match a {@link Uri} to a {@link MoviesUriEnum}.
 * <p />
 * All methods are thread safe, except {@link #buildUriMatcher()} and {@link #buildEnumsMap()},
 * which is why they are called only from the constructor.
 */
public class MoviesProviderUriMatcher {

    /**
     * All methods on a {@link UriMatcher} are thread safe, except {@code addURI}.
     */
    private UriMatcher mUriMatcher;

    private SparseArray<MoviesUriEnum> mEnumsMap = new SparseArray<>();

    /**
     * This constructor needs to be called from a thread-safe method as it isn't thread-safe itself.
     */
    public MoviesProviderUriMatcher(){
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        MoviesUriEnum[] uris = MoviesUriEnum.values();
        for (MoviesUriEnum uri : uris) {
            mUriMatcher.addURI(authority, uri.path, uri.code);
        }

        buildEnumsMap();
    }

    private void buildEnumsMap() {
        MoviesUriEnum[] uris = MoviesUriEnum.values();
        for (MoviesUriEnum uri : uris) {
            mEnumsMap.put(uri.code, uri);
        }
    }

    /**
     * Matches a {@code uri} to a {@link MoviesUriEnum}.
     *
     * @return the {@link MoviesUriEnum}, or throws new UnsupportedOperationException if no match.
     */
    public MoviesUriEnum matchUri(Uri uri){
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e){
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    /**
     * Matches a {@code code} to a {@link MoviesUriEnum}.
     *
     * @return the {@link MoviesUriEnum}, or throws new UnsupportedOperationException if no match.
     */
    public MoviesUriEnum matchCode(int code){
        MoviesUriEnum moviesUriEnum = mEnumsMap.get(code);
        if (moviesUriEnum != null){
            return moviesUriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
