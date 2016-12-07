package snowtam_ensim.snowtamreader2.network;

import android.content.Context;

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

    private static final String URL = "https://pilotweb.nas.faa.gov/PilotWeb/notamRetrievalByICAOAction.do?method=displayByICAOs";
    private static final int NB_OACI = 4;

    private MainActivity activity;
    private ArrayList<Snowtam> snowtams;
    private int nbSnowtamsRequested;

    public SnowtamRetrieval(MainActivity activity) {
        this.activity = activity;
        this.snowtams = new ArrayList<Snowtam>();
        this.nbSnowtamsRequested = 0;
    }

    public void retrieveSnowtams(Context context, final ArrayList<String> oacis) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Send the requests
        for (final String oaci : oacis) {
            StringRequest request = new StringRequest
                    (Request.Method.POST, URL, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            String regex = "<PRE>([^\\n]*\\n\\(SNOWTAM.*?)<\\/PRE>";
                            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                            Matcher matcher = pattern.matcher(response);

                            // Find the first occurrence of a SNOWTAM if any
                            if (matcher.find()) {
                                snowtams.add(new Snowtam(oaci.toUpperCase(), matcher.group(1)));
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

            requestQueue.add(request);
        }
    }

    private void sendResult() {
        if (nbSnowtamsRequested == NB_OACI) {
            activity.goToNextActivity(snowtams);
        }
    }

}
