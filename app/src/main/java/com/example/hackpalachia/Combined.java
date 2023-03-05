package com.example.hackpalachia;

import io.realm.RealmObject;
import org.bson.types.ObjectId;

public class Combined{

    private ObjectId _id;

    private String city;

    private String county_name;

    private String lat;

    private String lng;

    private String state_id;

    private String state_name;

    private String type;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountyName() { return county_name; }
    public void setCountyName(String county_name) { this.county_name = county_name; }

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLng() { return lng; }
    public void setLng(String lng) { this.lng = lng; }

    public String getStateId() { return state_id; }
    public void setStateId(String state_id) { this.state_id = state_id; }

    public String getStateName() { return state_name; }
    public void setStateName(String state_name) { this.state_name = state_name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
