/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Savelii Zagurskii
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.payqr.kladapiexample.rest;

import retrofit.http.GET;
import retrofit.http.Query;
import ru.payqr.kladapiexample.realm.models.kladr.RealmKladrSuggestion;

/**
 * @author Savelii Zagurskii
 */
public interface KladrService {
    @GET("/api.php?withParent=1&contentType=city&typeCode=7")
    RealmKladrSuggestion getCitySuggestionSync(@Query("query") String query,
                                               @Query("limit") int limit);

    @GET("/api.php?withParent=1&contentType=street")
    RealmKladrSuggestion getStreetSuggestionSync(@Query("query") String query,
                                                 @Query("limit") int limit,
                                                 @Query("cityId") String id);

    @GET("/api.php?withParent=1&contentType=building")
    RealmKladrSuggestion getBuildingSuggestionSync(@Query("query") String query,
                                                   @Query("limit") int limit,
                                                   @Query("streetId") String id);
}