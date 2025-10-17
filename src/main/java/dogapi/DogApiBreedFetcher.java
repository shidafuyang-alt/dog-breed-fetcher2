package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String BASE = "https://dog.ceo/api/breed/";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }
        String key = breed.toLowerCase(Locale.ROOT);
        String url = BASE + key + "/list";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException(key);
            }
            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");

            if ("success".equals(status)) {
                JSONArray arr = json.getJSONArray("message");
                List<String> out = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    out.add(arr.getString(i));
                }
                return out;
            } else if ("error".equals(status)) {
                throw new BreedNotFoundException(key);
            } else {
                throw new BreedNotFoundException(key);
            }
        } catch (IOException e) {
            throw new BreedNotFoundException(key);
        }
    }
}