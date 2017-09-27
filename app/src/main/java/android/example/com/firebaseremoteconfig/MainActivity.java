package android.example.com.firebaseremoteconfig;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Loading the Config Parameters or instance variables
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_params);

        firebaseRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build() );

        //To apply parameters to the user interface and
        //Handling changes at Startup
        firebaseRemoteConfig.activateFetched();
        applyConfig();
        firebaseRemoteConfig.fetch(0);
    }

    private void applyConfig() {
        //Get the widget from XML layout
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        TextView textview = (TextView) findViewById(R.id.textView);

        //Get the values form Firebase remote configuration
        String welcomeText = firebaseRemoteConfig.getString("welcome_text");
        String welcomeTextColor = firebaseRemoteConfig.getString("welcome_text_color");
        String layoutColor = firebaseRemoteConfig.getString("bg_color");

        // Set the properties from firebase remote configuration
        // If any value not set in firebase remote configuration then it gets from default set
        layout.setBackgroundColor(Color.parseColor(layoutColor));
        textview.setText(welcomeText);
        textview.setTextColor(Color.parseColor(welcomeTextColor));

    }

    private void updateConfg() {
        firebaseRemoteConfig.fetch(0).addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()) {
                          firebaseRemoteConfig.activateFetched();
                          applyConfig();
                      } else {
                          // Fetch failed
                      }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateConfg();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.updateConfig) {
            updateConfg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
