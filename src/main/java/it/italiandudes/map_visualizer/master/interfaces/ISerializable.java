package it.italiandudes.map_visualizer.master.interfaces;

import it.italiandudes.map_visualizer.master.utils.KeyParameters;
import org.json.JSONObject;

@SuppressWarnings("unused")
public interface ISerializable {
    String SERIALIZER_ID = "serializer_id";
    String DB_VERSION = KeyParameters.DB_VERSION;
    JSONObject exportElementJSON();
    String exportElement();
}
