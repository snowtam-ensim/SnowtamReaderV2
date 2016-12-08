package snowtam_ensim.snowtamreader2.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import snowtam_ensim.snowtamreader2.activities.MainActivity;
import snowtam_ensim.snowtamreader2.model.Snowtam;

/**
 * Created by Paul-Etienne FRANCOIS on 03/12/16.
 */
public class SnowtamRetrieval {

    private static final String SNOWTAM_CONTENT_URL = "https://pilotweb.nas.faa.gov/PilotWeb/notamRetrievalByICAOAction.do?method=displayByICAOs";
    private static final String SNOWTAM_COORDINATES_URL = "https://www.world-airport-codes.com/search?s=";

    private MainActivity activity;
    private ArrayList<Snowtam> snowtams;
    private int nbSnowtamsRequested;
    private ArrayList<String> oacis;

    public SnowtamRetrieval(MainActivity activity) {
        this.activity = activity;
        this.snowtams = new ArrayList<>();
        this.nbSnowtamsRequested = 0;
    }

    public void retrieveSnowtams(Context context, final ArrayList<String> oacis) {
        this.oacis = oacis;
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Send the requests
        for (final String oaci : oacis) {
            StringRequest contentRequest = new StringRequest
                    (Request.Method.POST, SNOWTAM_CONTENT_URL, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d("SnowtamReader", "Content received");
                            String regex = "<PRE>([^\\n]*\\n\\(SNOWTAM.*?)<\\/PRE>";
                            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                            Matcher matcher = pattern.matcher(response);

                            // Find the first occurrence of a SNOWTAM if any
                            if (matcher.find()) {
                                final Snowtam snowtam = new Snowtam(oaci.toUpperCase(), matcher.group(1));
                                snowtams.add(snowtam);

                                StringRequest coordinatesRequest = new StringRequest
                                        (Request.Method.GET, SNOWTAM_COORDINATES_URL + oaci, new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
                                                Log.d("SnowtamReader", "Coordinates received");
                                                // Retrieve the latitude and longitude
                                                String regex = "data-key=\"Latitude\" data-value=\"(.*?)\".*?data-key=\"Longitude\" data-value=\"(.*?)\"";
                                                Pattern pattern = Pattern.compile(regex);
                                                Matcher matcher = pattern.matcher(response);

                                                if (matcher.find()) {
                                                    Log.d("SnowtamReader", "Latitude : " + matcher.group(1) + ", Longitude : " + matcher.group(2));
                                                    snowtam.setLatitude(Double.parseDouble(matcher.group(1)));
                                                    snowtam.setLongitude(Double.parseDouble(matcher.group(2)));
                                                }

                                                // Retrieve the full airfield name
                                                regex = "<h1 class=\"airport-title\">\\s*(.*)\\s*&.*";
                                                pattern = Pattern.compile(regex);
                                                matcher = pattern.matcher(response);

                                                if (matcher.find()) {
                                                    Log.d("SnowtamReader", "Full name : " + matcher.group(1).trim());
                                                    snowtam.setLocation(matcher.group(1).trim());
                                                    snowtam.parseSnowtam();
                                                }

                                                nbSnowtamsRequested++;
                                                sendResult();
                                            }

                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                activity.displayToast("Erreur : " + error.getMessage());
                                            }
                                        });

                                requestQueue.add(coordinatesRequest);
                            }

                            nbSnowtamsRequested++;
                            sendResult();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            activity.displayToast("Erreur : " + error.getMessage());
                        }
                    }) {

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("formatType", "DOMESTIC");
                    params.put("reportType", "RAW");
                    params.put("actionType", "notamRetrievalByICAOs");
                    params.put("retrieveLocId", oaci);

                    return params;
                }
            };

            /*StringRequest coordinatesRequest = new StringRequest
                    (Request.Method.GET, SNOWTAM_COORDINATES_URL + oaci, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d("SnowtamReader", "Coordinates received");
                            String regex = "data-key=\"Latitude\" data-value=\"(.*?)\".*?data-key=\"Longitude\" data-value=\"(.*?)\"";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(response);

                            if (matcher.find()) {
                                Log.d("SnowtamReader", "Latitude : " + matcher.group(1) + ", Longitude : " + matcher.group(2));
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            activity.displayToast("Erreur : " + error.getMessage());
                        }
                    });*/

            requestQueue.add(contentRequest);
            //requestQueue.add(coordinatesRequest);
        }
    }

    private void sendResult() {
        if (nbSnowtamsRequested == oacis.size() * 2) {
            Log.d("SnowtamReader", "Sending data");
            activity.goToNextActivity(snowtams);
        }
    }

}
