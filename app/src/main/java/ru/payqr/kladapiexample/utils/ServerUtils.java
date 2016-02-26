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

package ru.payqr.kladapiexample.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import ru.payqr.kladapiexample.interfaces.OnSuggestionsListener;
import ru.payqr.kladapiexample.realm.models.cache.KladrUserQuery;
import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;
import ru.payqr.kladapiexample.realm.models.kladr.RealmKladrSuggestion;
import ru.payqr.kladapiexample.rest.ContentType;
import ru.payqr.kladapiexample.rest.KladrBody;
import ru.payqr.kladapiexample.rest.KladrRestClient;

/**
 * @author Savelii Zagurskii
 */
public class ServerUtils {
    // Executor that runs queries in a queue
    private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Query Kladr for current query.
     *
     * @param query       your query to process.
     * @param contentType the type of query
     * @param id          the id of parent if present
     * @param listener    listener to get callback on ready suggestions.
     */
    public static void query(final ContentType contentType, final String query, final String id, final OnSuggestionsListener listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Trim current query for ignoring whitespaces
                String queryFromUser = query.replaceAll("\\s+", " ").trim();

                // If the query is not empty, we proceed
                if (!queryFromUser.isEmpty()) {
                    // Get default instance of Realm
                    Realm realm = Realm.getDefaultInstance();

                    // Query realm for current user query.
                    // If it is cached, we will get results.
                    // Otherwise, we need to query over the Internet
                    RealmResults<KladrUserQuery> kladrUserQueryRealmResults = realm.where(KladrUserQuery.class)
                            .equalTo(KladrUserQuery.COLUMN_QUERY, queryFromUser)
                            .equalTo(KladrUserQuery.COLUMN_OBJECT_ID, String.valueOf(id))
                            .equalTo(KladrUserQuery.COLUMN_CONTENT_TYPE, contentType.name())
                            .findAll();

                    // Initialize the list of suggestions
                    // which will be used to generate
                    // the required UUIDs for the list on the UI
                    final List<KladrResult> suggestions = new ArrayList<>();

                    boolean success = false;

                    // UUIDs of the KladrResults that need to be filled
                    // in AutoCompleteTextView
                    List<String> ids = new ArrayList<>();

                    // If we have no cache on this query,
                    // we have to query over the Internet
                    if (kladrUserQueryRealmResults.size() == 0) {
                        dispatchReset(contentType, listener);

                        RealmKladrSuggestion suggestion = null;
                        try {
                            suggestion = getSuggestionFromServer(queryFromUser, contentType, id);

                            success = true;
                        } catch (RetrofitError e) {
                            e.printStackTrace();

                            dispatchError(e.getMessage(), listener);
                        } catch (Exception e) {
                            e.printStackTrace();

                            dispatchError(e.getMessage(), listener);
                        }

                        // Cache if success
                        if (success) {
                            cacheUserQueryWithServerResult(queryFromUser, realm, suggestions, suggestion, contentType, id);
                        }
                        // Fill from Internet
                        fillSuggestionsWithUuids(suggestions, ids);
                    } else {
                        // Fill from cache
                        fillSuggestionsFromCache(kladrUserQueryRealmResults, ids);
                    }

                    // Close current open Realm instance
                    realm.close();

                    // Update suggestions
                    dispatchUpdate(contentType, ids, listener);
                }
            }
        };
        executor.submit(runnable);
        //new Thread(runnable).start();
    }

    /**
     * Get kladr suggestion with the specified parameters synchronously.
     *
     * @param queryFromUser the query specified by the user.
     * @param contentType   the type of query.
     * @param id            the id of parent if present.
     * @return suggestion from the server.
     */
    private static RealmKladrSuggestion getSuggestionFromServer(String queryFromUser, ContentType contentType, String id) {
        RealmKladrSuggestion suggestion = null;
        // Synchronously get the answer from Kladr
        if (contentType == ContentType.city) {
            suggestion = KladrRestClient.getInstance().getCitySuggestion(new KladrBody(queryFromUser, 10));
            suggestion.setUuid(UUID.randomUUID().toString());

        } else if (contentType == ContentType.street) {
            suggestion = KladrRestClient.getInstance().getStreetSuggestion(new KladrBody(queryFromUser, 10, id));
            suggestion.setUuid(UUID.randomUUID().toString());

        } else if (contentType == ContentType.building) {
            suggestion = KladrRestClient.getInstance().getBuildingSuggestion(new KladrBody(queryFromUser, 10, id));
            suggestion.setUuid(UUID.randomUUID().toString());
        }
        return suggestion;
    }

    /**
     * Fill the UUIDs of the kladr suggestions.
     *
     * @param suggestions the suggestions that came either from cache or Internet.
     * @param ids         the list of UUIDs that needs to be filled and passed to the UI.
     */
    private static void fillSuggestionsWithUuids(List<KladrResult> suggestions, List<String> ids) {
        for (int i = 0; i < suggestions.size(); i++) {
            ids.add(suggestions.get(i).getUuid());
        }
    }

    /**
     * Dispatch reset the list of the AutoCompleteTextView.
     *
     * @param listener listener to get callback on resetting suggestions.
     */
    private static void dispatchReset(final ContentType contentType, final OnSuggestionsListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onResultsReset(contentType);
            }
        });
    }

    /**
     * Cache Kladr answer to Realm.
     *
     * @param queryFromUser               trimmed user query.
     * @param realm                       instance of Realm.
     * @param suggestions                 list of suggestions to be filled.
     * @param contentType                 type of content (city, street, building, etc.)
     * @param id                          object id, if present.
     * @param kladrServerSuggestionAnswer an object that corresponds the answer from Kladr.
     */
    private static void cacheUserQueryWithServerResult(String queryFromUser,
                                                       Realm realm,
                                                       List<KladrResult> suggestions,
                                                       RealmKladrSuggestion kladrServerSuggestionAnswer,
                                                       ContentType contentType,
                                                       String id) {
        if (kladrServerSuggestionAnswer != null) {
            realm.beginTransaction();

            KladrUserQuery kladrUserQuery = realm.createObject(KladrUserQuery.class);

            kladrUserQuery.setUuid(UUID.randomUUID().toString());
            kladrUserQuery.setQuery(queryFromUser);
            kladrUserQuery.setContentType(contentType.name());
            kladrUserQuery.setObjectId(String.valueOf(id));

            for (int i = 0; i < kladrServerSuggestionAnswer.getKladrResults().size(); i++) {
                KladrResult suggestion = kladrServerSuggestionAnswer.getKladrResults().get(i);

                // Initialize primary keys
                initializePrimaryKeys(suggestion);

                // Fill suggestions list that will be passed to UI
                suggestions.add(suggestion);

                // Fill suggestion to cache
                kladrUserQuery.getResults().add(suggestion);
            }

            realm.commitTransaction();
        }
    }

    /**
     * Initialize all primary keys in all instances of {@link KladrResult}.
     *
     * @param suggestion instance of {@code KladrResult} that needs to be initialized with new primary keys.
     */
    private static void initializePrimaryKeys(KladrResult suggestion) {
        // Initialize primary keys in all possible KladrResult,
        // so that we have everything unique
        suggestion.setUuid(UUID.randomUUID().toString());

        for (int j = 0; j < suggestion.getParents().size(); j++) {
            suggestion.getParents().get(j).setUuid(UUID.randomUUID().toString());
        }
    }

    /**
     * Send callback that notifies about something went wrong.
     *
     * @param message  error message.
     * @param listener listener to get callback on error.
     */
    private static void dispatchError(final String message, final OnSuggestionsListener listener) {
        if (listener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(message);
                }
            });
        }
    }

    /**
     * Fill a list of suggestions with data from cache.
     *
     * @param kladrUserQueryRealmResults query with cache results.
     * @param suggestions                list which needs to be filled.
     */
    private static void fillSuggestionsFromCache(RealmResults<KladrUserQuery> kladrUserQueryRealmResults, List<String> suggestions) {
        for (int i = 0; i < kladrUserQueryRealmResults.size(); i++) {
            RealmList<KladrResult> results = kladrUserQueryRealmResults.get(i).getResults();

            for (int j = 0; j < results.size(); j++) {
                suggestions.add(results.get(j).getUuid());
            }
        }
    }

    /**
     * Dispatch update to UI.
     *
     * @param suggestions suggestions that need to be passed to UI.
     * @param listener    listener to get callback on ready suggestions.
     */
    private static void dispatchUpdate(final ContentType contentType, final List<String> suggestions, final OnSuggestionsListener listener) {
        if (listener != null && suggestions.size() > 0) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuggestionsReady(contentType, suggestions);
                }
            });
        }
    }
}
