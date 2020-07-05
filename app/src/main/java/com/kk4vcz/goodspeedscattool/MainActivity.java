package com.kk4vcz.goodspeedscattool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RadioState.mainActivity=this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_codeplug, R.id.nav_cat, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ProgressBar progressBar=findViewById(R.id.progressBar);
        RadioState.progressBar=progressBar;


        // Get the app's shared preferences
        RadioState.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RadioState.updatePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RadioTask asyncTask;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_importcodeplug:

                return true;
            case R.id.action_exportcodeplug:

                return true;
            case R.id.action_downloadcodeplug:
                //TODO: This takes a while.  It should have a progress bar.
                asyncTask=RadioTask.newDownloadCodeplugTask();
                asyncTask.execute();
                return true;
            case R.id.action_uploadcodeplug:
                //TODO: This takes a while.  It should have a progress bar.
                asyncTask=RadioTask.newUploadCodeplugTask();
                asyncTask.execute();
                return true;
            case R.id.action_emptylocalcodeplug:

                return true;
            case R.id.action_erasetargetcodeplug:
                //TODO: This takes a while.  It should have a progress bar.
                asyncTask=RadioTask.newEraseTargetCodeplugTask();
                asyncTask.execute();
                return true;
            default:
                Log.e("Menu", "Unhandled menu item.");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}