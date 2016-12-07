package snowtam_ensim.snowtamreader2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import snowtam_ensim.snowtamreader2.R;
import snowtam_ensim.snowtamreader2.model.Snowtam;
import snowtam_ensim.snowtamreader2.network.SnowtamRetrieval;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void validate(View v) {
        ArrayList<String> oaci = new ArrayList<String>();
        if (!((EditText) findViewById(R.id.oaci_editText1)).getText().toString().equals(""))
            oaci.add(((EditText) findViewById(R.id.oaci_editText1)).getText().toString());
        if (!((EditText) findViewById(R.id.oaci_editText2)).getText().toString().equals(""))
            oaci.add(((EditText) findViewById(R.id.oaci_editText2)).getText().toString());
        if (!((EditText) findViewById(R.id.oaci_editText3)).getText().toString().equals(""))
            oaci.add(((EditText) findViewById(R.id.oaci_editText3)).getText().toString());
        if (!((EditText) findViewById(R.id.oaci_editText4)).getText().toString().equals(""))
            oaci.add(((EditText) findViewById(R.id.oaci_editText4)).getText().toString());

        SnowtamRetrieval service = new SnowtamRetrieval(this);
        if (!oaci.isEmpty())
            service.retrieveSnowtams(getApplicationContext(), oaci);
        else
            displayToast(getString(R.string.error_fill_fields));
    }

    public void goToNextActivity(ArrayList<Snowtam> snowtams) {
        Intent intent = new Intent(getApplicationContext(), SnowtamDetailActivity.class);
        intent.putParcelableArrayListExtra("Snowtams", snowtams);

        startActivity(intent);
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
