package ru.payqr.kladapiexample.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import ru.payqr.dadataexample.R;
import ru.payqr.kladapiexample.interfaces.OnSuggestionsListener;
import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;
import ru.payqr.kladapiexample.rest.ContentType;

/**
 * @author Savelii Zagurskii
 */
public class KladrTextChangedListener implements TextWatcher, AdapterView.OnItemClickListener {

    private int id;
    private EditText editText;
    private ContentType contentType;
    private OnSuggestionsListener listener;
    private String selectedId;

    public KladrTextChangedListener(EditText editText, OnSuggestionsListener listener) {
        this.id = editText.getId();
        this.editText = editText;
        contentType = ContentType.unidentified;
        initContentType();
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (contentType == ContentType.city)
            ServerUtils.query(contentType, s.toString(), "-1", listener);
        else if (contentType == ContentType.street)
            ServerUtils.query(contentType, s.toString(), selectedId, listener);
        else if (contentType == ContentType.building)
            ServerUtils.query(contentType, s.toString(), selectedId, listener);
    }

    private void initContentType() {
        if (id == R.id.autocompletetextview_activitymain_city) {
            contentType = ContentType.city;
        } else if (id == R.id.autocompletetextview_activitymain_street) {
            contentType = ContentType.street;
        } else if (id == R.id.autocompletetextview_activitymain_building) {
            contentType = ContentType.building;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KladrResult result = (KladrResult) parent.getItemAtPosition(position);

        selectedId = result.getId();
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }
}