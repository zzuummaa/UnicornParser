import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class PinterestPareser {
    private String urlBase = "https://www.pinterest.ru/";

    public ArrayList<ImageData> extractImages(String pinId) throws IOException {
        String url = urlBase + "pin/" + pinId + "/";
        Connection.Response resp = Jsoup.connect(url).userAgent("Opera/9.80 (X11; Linux i686; Ubuntu/14.10) " +
                "Presto/2.12.388 Version/12.16").execute();

        Document doc = resp.parse();
        String jsonContent = doc.getElementsByAttributeValue("id", "jsInit1").first().html();
        return extractImagesFromjsInit1(jsonContent, pinId);
    }

    public ArrayList<ImageData> extractImagesFromjsInit1(String jsonContent, String pinId) {
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray resources = jsonObject.getJSONArray("resourceDataCache");
        ArrayList<ImageData> images = new ArrayList<>();
        ArrayList<JSONObject> imageData = new ArrayList<>();

        for (int i = 0; i < resources.length(); i++) {
            JSONObject data = resources.getJSONObject(i).getJSONObject("data");

            if (!data.has("id")) {
                System.out.println("Extract node " + i + " - failed");
            } else {
                imageData.add(data);
                System.out.println("Extract node " + i + " - success");
            }

            if (data.has("related_pins_feed")) {
                JSONArray relatedPinsFeed = data.getJSONArray("related_pins_feed");
                for (int j = 0; j < relatedPinsFeed.length(); j++) {
                    JSONObject relatedData = relatedPinsFeed.getJSONObject(j);
                    if (!relatedData.has("id")) {
                        System.out.println("Extract node " + i + "." + j + " - failed");
                    } else {
                        imageData.add(relatedData);
                        System.out.println("Extract node " + i + "." + j + " - success");
                    }
                }
            }
        }

        for (JSONObject data: imageData) {
            String id = data.getString("id");
            String url = data.getJSONObject("images").getJSONObject("orig").getString("url");

            if (pinId.equals(id)) {
                images.add(0, new ImageData(id, url));
            } else {
                images.add(new ImageData(id, url));
            }
        }

        return images;
    }
}
