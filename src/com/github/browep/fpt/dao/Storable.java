package com.github.browep.fpt.dao;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.browep.fpt.util.CollectionsUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/27/11
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Storable implements Parcelable{
    private Map<String, Object> map = new HashMap<String, Object>();
    private int id;
    private Date modified;
    private Date created;

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new ObjectMapper()).writeValue(baos,map);
        return baos.toString();
    }


    public Storable(int id, Date created, Date modified, String data) throws IOException, JSONException {
        this(data);
        this.id = id;
        this.created = created;
        this.modified = modified;

    }

    public Storable(String data) throws JSONException, IOException {
        setData(data);
    }

    public Storable() {
        modified = new Date();
        created = new Date();
    }

    public Object get(String propName) {
        return map.get(propName);
    }

    public Map getMap(String propName){
        return (Map) get(propName);
    }

    public void put(String propName, Object propValue) {
        map.put(propName, propValue);
        modified = new Date();
    }

    public List getList(String propName){
        return (List) get(propName);
    }

    public int getId(){
        return id;
    }

    public abstract int getType();

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(String data) throws IOException {
        map = (new ObjectMapper()).readValue(data, HashMap.class);
    }

    public abstract List<String> getIndexBys();

    public String toString(){
        String data = map.toString();
        String modifiedStr = "";
        String createdStr="";
        try {
            modifiedStr = modified.toString();
            createdStr = created.toString();
            data = serialize();
        } catch (Exception e){        }
        return "Type:" + getType() + " created: " + createdStr + " modified: " + modifiedStr + " data:" + data;
    }

    protected String toIndexPath(String propName, String defaultValue) {
        Object value = get(propName);
        if (value == null) {
            value = defaultValue;
        }
        return propName + "_" + value;
    }

}