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

package ru.payqr.kladapiexample.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.payqr.dadataexample.R;
import ru.payqr.kladapiexample.interfaces.OnSuggestionsListener;
import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;
import ru.payqr.kladapiexample.rest.ContentType;
import ru.payqr.kladapiexample.utils.KladrTextChangedListener;

public class ActivityMain extends AppCompatActivity implements OnSuggestionsListener {

    private static final String BUNDLE_CITY = "bundle_city";
    private static final String BUNDLE_STREET = "bundle_street";
    private static final String BUNDLE_BUILDING = "bundle_building";

    private static final String BUNDLE_SELECTED_CITY = "bundle_selected_city";
    private static final String BUNDLE_SELECTED_STREET = "bundle_selected_street";

    private static final String BUNDLE_SELECTED_EDITTEXT = "bundle_selected_editext";

    private static final int NONE_FOCUSED = -1;
    private static final int CITY_FOCUSED = 0;
    private static final int STREET_FOCUSED = 1;
    private static final int BUILDING_FOCUSED = 2;

    private KladrAutoCompleteAdapter adapterCityCustom;
    private KladrAutoCompleteAdapter adapterStreetCustom;
    private KladrAutoCompleteAdapter adapterBuildingCustom;

    private AutoCompleteTextView city;
    private AutoCompleteTextView street;
    private AutoCompleteTextView building;

    private Toolbar toolbar;

    private KladrTextChangedListener cityTextListener;
    private KladrTextChangedListener streetTextListener;
    private KladrTextChangedListener buildingTextListener;

    private Toast toast;

    private Realm realm;
    private RealmResults<KladrResult> resultsCity;
    private RealmResults<KladrResult> resultsStreet;
    private RealmResults<KladrResult> resultsBuilding;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Realm database and get Realm result for each adapter
        realm = Realm.getDefaultInstance();
        resultsCity = realm.where(KladrResult.class).equalTo(KladrResult.COLUMN_CONTENT_TYPE, ContentType.city.name()).findAll();
        resultsStreet = realm.where(KladrResult.class).equalTo(KladrResult.COLUMN_CONTENT_TYPE, ContentType.street.name()).findAll();
        resultsBuilding = realm.where(KladrResult.class).equalTo(KladrResult.COLUMN_CONTENT_TYPE, ContentType.building.name()).findAll();

        // Initialize all edit texts
        city = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_activitymain_city);
        street = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_activitymain_street);
        building = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_activitymain_building);

        // Initialize material toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize all autocomplete adapters
        adapterCityCustom = new KladrAutoCompleteAdapter(this, resultsCity);
        adapterStreetCustom = new KladrAutoCompleteAdapter(this, resultsStreet);
        adapterBuildingCustom = new KladrAutoCompleteAdapter(this, resultsBuilding);

        // Set the adapters to edit texts
        city.setAdapter(adapterCityCustom);
        street.setAdapter(adapterStreetCustom);
        building.setAdapter(adapterBuildingCustom);

        // Initialize shared preferences for getting and saving values on start and exit respectively
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize text changed listeners
        cityTextListener = new KladrTextChangedListener(city, this);
        streetTextListener = new KladrTextChangedListener(street, this);
        buildingTextListener = new KladrTextChangedListener(building, this);

        // If coming from saved instance, init from it
        if (savedInstanceState != null) {
            city.setText(savedInstanceState.getString(BUNDLE_CITY));
            street.setText(savedInstanceState.getString(BUNDLE_STREET));
            building.setText(savedInstanceState.getString(BUNDLE_BUILDING));

            // This is not an error. See onResume().
            streetTextListener.setSelectedId(savedInstanceState.getString(BUNDLE_SELECTED_CITY));
            buildingTextListener.setSelectedId(savedInstanceState.getString(BUNDLE_SELECTED_STREET));

            int lastFocusedEditText = savedInstanceState.getInt(BUNDLE_SELECTED_EDITTEXT);
            focusEditText(lastFocusedEditText);
        } else {
            city.setText(sharedPreferences.getString(BUNDLE_CITY, ""));
            street.setText(sharedPreferences.getString(BUNDLE_STREET, ""));
            building.setText(sharedPreferences.getString(BUNDLE_BUILDING, ""));

            // This is not an error. See onResume().
            streetTextListener.setSelectedId(sharedPreferences.getString(BUNDLE_SELECTED_CITY, ""));
            buildingTextListener.setSelectedId(sharedPreferences.getString(BUNDLE_SELECTED_STREET, ""));

            int lastFocusedEditText = sharedPreferences.getInt(BUNDLE_SELECTED_EDITTEXT, NONE_FOCUSED);
            focusEditText(lastFocusedEditText);
        }

        // After initializing edit texts, set the selection to the last character
        city.setSelection(city.length());
        street.setSelection(street.length());
        building.setSelection(building.length());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Add the text changed listener in onResume
        // because they will be fully initialized here
        city.addTextChangedListener(cityTextListener);
        street.addTextChangedListener(streetTextListener);
        building.addTextChangedListener(buildingTextListener);

        // Warning! This is not an error.
        // We need to listen to OnClick events on the next EditText
        // in order to save the selected value.
        city.setOnItemClickListener(streetTextListener);
        street.setOnItemClickListener(buildingTextListener);
        building.setOnItemClickListener(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove all the text changed listeners
        city.removeTextChangedListener(cityTextListener);
        street.removeTextChangedListener(streetTextListener);
        building.removeTextChangedListener(buildingTextListener);

        // Save the entered values by user to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(BUNDLE_CITY, city.getText().toString());
        editor.putString(BUNDLE_STREET, street.getText().toString());
        editor.putString(BUNDLE_BUILDING, building.getText().toString());

        // This is not an error. See onResume().
        editor.putString(BUNDLE_SELECTED_CITY, streetTextListener.getSelectedId());
        editor.putString(BUNDLE_SELECTED_STREET, buildingTextListener.getSelectedId());

        editor.putInt(BUNDLE_SELECTED_EDITTEXT, getCurrentFocusedEditText());

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Destroy all the remaining resources
        if (adapterCityCustom != null) {
            adapterCityCustom.onDestroy();
        }

        if (adapterStreetCustom != null) {
            adapterStreetCustom.onDestroy();
        }

        if (adapterBuildingCustom != null) {
            adapterBuildingCustom.onDestroy();
        }

        if (realm != null) {
            realm.close();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save values to outState
        outState.putString(BUNDLE_CITY, city.getText().toString());
        outState.putString(BUNDLE_STREET, street.getText().toString());
        outState.putString(BUNDLE_BUILDING, building.getText().toString());

        outState.putString(BUNDLE_SELECTED_CITY, cityTextListener.getSelectedId());
        outState.putString(BUNDLE_SELECTED_STREET, streetTextListener.getSelectedId());

        outState.putInt(BUNDLE_SELECTED_EDITTEXT, getCurrentFocusedEditText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activitymain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearall) {

            city.getText().clear();
            street.getText().clear();
            building.getText().clear();

            city.requestFocus();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public synchronized void onSuggestionsReady(ContentType contentType, List<String> suggestions) {
        // Update the suggestion in the specified edit text
        if (contentType == ContentType.city)
            updateSuggestions(adapterCityCustom, suggestions);

        else if (contentType == ContentType.street)
            updateSuggestions(adapterStreetCustom, suggestions);

        else if (contentType == ContentType.building)
            updateSuggestions(adapterBuildingCustom, suggestions);
    }

    @Override
    public void onError(String message) {
        // Cancel current toast for
        // showing only one toast at a time
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResultsReset(ContentType contentType) {
        if (contentType == ContentType.city)
            adapterCityCustom.reset();

        else if (contentType == ContentType.street)
            adapterStreetCustom.reset();

        else if (contentType == ContentType.building)
            adapterBuildingCustom.reset();
    }

    private void updateSuggestions(KladrAutoCompleteAdapter adapter, List<String> suggestions) {
        adapter.addAll(suggestions);
    }

    private int getCurrentFocusedEditText() {
        int currentFocusedEditText = NONE_FOCUSED;
        if (city.isFocused())
            currentFocusedEditText = CITY_FOCUSED;
        else if (street.isFocused())
            currentFocusedEditText = STREET_FOCUSED;
        else if (building.isFocused())
            currentFocusedEditText = BUILDING_FOCUSED;
        return currentFocusedEditText;
    }

    private void focusEditText(int editText) {
        switch (editText) {
            case CITY_FOCUSED:
                city.requestFocus();
                break;
            case STREET_FOCUSED:
                street.requestFocus();
                break;
            case BUILDING_FOCUSED:
                building.requestFocus();
                break;
        }
    }
}