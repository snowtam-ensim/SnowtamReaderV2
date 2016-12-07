package snowtam_ensim.snowtamreader2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import snowtam_ensim.snowtamreader2.R;

/**
 * Created by Raphaël on 24/11/2016.
 */

public class SnowtamDisplayActivity extends AppCompatActivity
{
    final String EXTRA_OACI = "OACI";
    final public String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    final public String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snowtam_display);

        Intent intent = getIntent();
        TextView snowtam = (TextView) findViewById(R.id.displaySNOWTAM);

        if (intent != null) {
            snowtam.setText(intent.getStringExtra(EXTRA_OACI));
        }

        // Go to the map
        final Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                double latitude = 48;
                double longitude = 0.2;
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(EXTRA_LATITUDE, latitude);
                intent.putExtra(EXTRA_LONGITUDE, longitude);
                startActivity(intent);

            }
        });
    }
}