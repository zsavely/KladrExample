package ru.payqr.kladapiexample.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;
import ru.payqr.kladapiexample.utils.FormatUtils;

/**
 * @author Savelii Zagurskii
 */
public class KladrAutoCompleteTextView extends AutoCompleteTextView {
    public KladrAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Returns the country name corresponding to the selected item
     */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        KladrResult result = (KladrResult) selectedItem;
        return FormatUtils.getAutoCompleteFormat(result);
    }
}
