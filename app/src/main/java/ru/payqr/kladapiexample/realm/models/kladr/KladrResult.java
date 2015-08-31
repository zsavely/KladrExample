package ru.payqr.kladapiexample.realm.models.kladr;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Savelii Zagurskii
 */
public class KladrResult extends RealmObject {
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_CONTENT_TYPE = "contentType";

    @PrimaryKey
    private String uuid;

    @Index
    private String name;

    private String id;
    private String zip;
    private String type;
    private String typeShort;
    private String okato;
    private String contentType;
    private RealmList<KladrResult> parents;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeShort() {
        return typeShort;
    }

    public void setTypeShort(String typeShort) {
        this.typeShort = typeShort;
    }

    public String getOkato() {
        return okato;
    }

    public void setOkato(String okato) {
        this.okato = okato;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public RealmList<KladrResult> getParents() {
        return parents;
    }

    public void setParents(RealmList<KladrResult> parents) {
        this.parents = parents;
    }
}