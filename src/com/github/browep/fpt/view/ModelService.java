package com.github.browep.fpt.view;

import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.R;
import com.github.browep.fpt.Workout;
import com.github.browep.fpt.util.Log;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelService {
  private Map<String, Map> models;

  private Map<Integer, String> idsToName = new HashMap<Integer, String>();

  public ModelService(FptApp fptApp) {
    InputStream inputStream =
        fptApp.getApplicationContext().getResources().openRawResource(R.raw.model);
    try {
      models = (Map<String, Map>) (new ObjectMapper()).readValue(inputStream, HashMap.class).get("models");

      for (Map.Entry<String, Map> entry : models.entrySet()) {
        idsToName.put((Integer) entry.getValue().get("id"), entry.getKey());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public Map<String, Map> getPropertyDefinition(Integer modelType) {
    String typeName = idsToName.get(modelType);
    return getPropertyDefinition(typeName);
  }

  public Map<String, Map> getPropertyDefinition(String typeName) {
    // get name
    Map model = models.get(typeName);
    Map<String, Map> properties = (Map<String, Map>) model.get("properties");
    if (model.containsKey("parent")) {
      properties.putAll(getPropertyDefinition((String) model.get("parent")));
    }
    return properties;
  }

  public Integer getAddDataViewId(Integer modelId){
    Map model = getModel(modelId);
    return (Integer) model.get("add_data_template_id");
  }

  public Map<String,Object> getModel(Integer modelId){
    String modelName = idsToName.get(modelId);
    return models.get(modelName);

  }

  public Workout fillWorkout(View view,Workout workout){
    Map<String,Map> properties = getPropertyDefinition(workout.getType());
    for(Map.Entry<String,Map> property : properties.entrySet()){
      // get the view id for the property
      String propertyName = property.getKey();
      Map<String,Object> attributes = property.getValue();
      // get the value, depending on what we are expecting
      Integer viewId = (Integer) attributes.get("view_id");

      EditText propertyView = (EditText) view.findViewById(viewId);
      if("integer".equals(attributes.get("type"))){
        Integer value = Integer.parseInt(propertyView.getText().toString());
        workout.put(propertyName,value);
      }
    }

    return workout;

  }

}
