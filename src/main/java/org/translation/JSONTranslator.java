package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> translator = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Map<String, String> languages = new HashMap<>();
                String countryCode = "";

                for (String key : jsonObject.keySet()) {
                    if ("alpha3".equals(key)) {
                        countryCode = jsonObject.getString(key);
                    }
                    else if (!("alpha2".equals(key) || "id".equals(key))) {
                        languages.put(key, jsonObject.getString(key));
                    }
                }

                this.translator.put(countryCode, languages);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        return new ArrayList<>(this.translator.get(country).keySet());
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(this.translator.keySet());
    }

    @Override
    public String translate(String country, String language) {
        return this.translator.get(country).get(language);
    }
}
