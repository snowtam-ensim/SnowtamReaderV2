package snowtam_ensim.snowtamreader2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{

    final String EXTRA_OACI = "OACI";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText oaci = (EditText) findViewById(R.id.searchOACI);
        final Button btnValidate = (Button) findViewById(R.id.btnValidate);

        btnValidate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SnowtamDisplayActivity.class);
                intent.putExtra(EXTRA_OACI, oaci.getText().toString());
                startActivity(intent);

            }
        });
    }
}
