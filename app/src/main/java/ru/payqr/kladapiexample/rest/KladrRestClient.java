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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import ru.payqr.dadataexample.BuildConfig;
import ru.payqr.kladapiexample.realm.models.kladr.RealmKladrSuggestion;

/**
 * @author Savelii Zagurskii
 */
public class KladrRestClient {
    private static final String BASE_URL = "http://kladr-api.ru";

    private static volatile KladrRestClient instance;

    private KladrService apiService;

    private KladrRestClient() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("token", BuildConfig.KLADR_TOKEN_KEY);
                        request.addQueryParam("key", BuildConfig.KLADR_API_KEY);
                    }
                })
                .setConverter(new GsonConverter(gson))
                .build();

        apiService = restAdapter.create(KladrService.class);
    }

    /**
     * Get default instance of {@code KladrRestClient}.
     *
     * @return an instance of KladrRestClient.
     */
    public static KladrRestClient getInstance() {
        KladrRestClient localInstance = instance;
        if (localInstance == null) {
            synchronized (KladrRestClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new KladrRestClient();
                }
            }
        }
        return localInstance;
    }

    /**
     * Get suggestion synchronously.
     *
     * @param body an object that need to be passed in the body of the request.
     * @return
     */
    public RealmKladrSuggestion getCitySuggestion(KladrBody body) {
        return apiService.getCitySuggestionSync(body.getQuery(), body.getLimit());
    }

    /**
     * Get suggestion synchronously.
     *
     * @param body an object that need to be passed in the body of the request.
     * @return
     */
    public RealmKladrSuggestion getStreetSuggestion(KladrBody body) {
        return apiService.getStreetSuggestionSync(body.getQuery(), body.getLimit(), body.getSelectedId());
    }

    /**
     * Get suggestion synchronously.
     *
     * @param body an object that need to be passed in the body of the request.
     * @return
     */
    public RealmKladrSuggestion getBuildingSuggestion(KladrBody body) {
        return apiService.getBuildingSuggestionSync(body.getQuery(), body.getLimit(), body.getSelectedId());
    }
}