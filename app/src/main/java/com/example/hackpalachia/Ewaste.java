package com.example.hackpalachia;

import io.realm.RealmObject;
import org.bson.types.ObjectId;

public class Ewaste {
    private ObjectId _id;
    private String LandfillName;
    private String Latitude;
    private String Longitude;
    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }
    public String getLandfillName() { return LandfillName; }
    public void setLandfillName(String LandfillName) { this.LandfillName = LandfillName; }
    public String getLatitude() { return Latitude; }
    public void setLatitude(String Latitude) { this.Latitude = Latitude; }
    public String getLongitude() { return Longitude; }
    public void setLongitude(String Longitude) { this.Longitude = Longitude; }
}
