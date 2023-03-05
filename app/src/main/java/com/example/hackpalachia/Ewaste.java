package com.example.hackpalachia;

import io.realm.RealmObject;
import org.bson.types.ObjectId;

public class Ewaste {
    //@PrimaryKey
    private ObjectId _id;

    private String location;

    private String more;

    private String type;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMore() { return more; }
    public void setMore(String more) { this.more = more; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
