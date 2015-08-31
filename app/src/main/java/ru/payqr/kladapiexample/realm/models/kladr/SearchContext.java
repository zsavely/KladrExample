package ru.payqr.kladapiexample.realm.models.kladr;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Savelii Zagurskii
 */
public class SearchContext extends RealmObject {

    @PrimaryKey
    private String uuid;

    private String contentType;
    private String query;
    private String withParent;
    private long limit;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getWithParent() {
        return withParent;
    }

    public void setWithParent(String withParent) {
        this.withParent = withParent;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}