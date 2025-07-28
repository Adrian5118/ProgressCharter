package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface JSONSerializable {
    public JSONObject toJSONObject() throws JSONException;
    public void fromJSONObject(JSONObject object) throws JSONException;
}
