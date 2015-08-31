package ru.payqr.kladapiexample.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;
import ru.payqr.kladapiexample.utils.FormatUtils;

/**
 * @author Savelii Zagurskii
 */
public class KladrAutoCompleteAdapter extends RealmBaseAdapter<KladrResult> implements Filterable {

    private final Context mContext;
    private RealmResults<KladrResult> mResults;
    private Realm realm;

    public KladrAutoCompleteAdapter(Context context, RealmResults<KladrResult> results) {
        super(context, results, true);
        mContext = context;
        mResults = results;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        KladrResult result = getItem(position);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(FormatUtils.getAutoCompleteFormat(result));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<KladrResult> books = mResults;
                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    /**
     * Release realm resources
     */
    public void onDestroy() {
        if (realm != null) {
            realm.close();
        }
    }

    /**
     * Configure realm results to show specified results
     *
     * @param ids the UUIDs which will be shown
     */
    public void addAll(List<String> ids) {
        RealmQuery<KladrResult> resultRealmQuery = realm.where(KladrResult.class);

        for (int i = 0; i < ids.size(); i++) {
            resultRealmQuery.equalTo(KladrResult.COLUMN_UUID, ids.get(i));
            if (i != ids.size() - 1)
                resultRealmQuery.or();
        }

        realmResults = resultRealmQuery.findAll();

        notifyDataSetChanged();
    }

    /**
     * Reset the realm results to show nothing
     */
    public void reset() {
        RealmQuery<KladrResult> resultRealmQuery = realm.where(KladrResult.class);

        // There are no column content types that equal "-1",
        // so it will show nothing
        resultRealmQuery.equalTo(KladrResult.COLUMN_CONTENT_TYPE, "-1");

        realmResults = resultRealmQuery.findAll();

        notifyDataSetChanged();
    }
}