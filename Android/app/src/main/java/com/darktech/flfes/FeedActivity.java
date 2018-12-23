package com.darktech.flfes;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FeedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new OffersFeedFragment()).commit();
        bottomNav.setOnNavigationItemSelectedListener(bottomNavItemSelected);

    }

   private BottomNavigationView.OnNavigationItemSelectedListener bottomNavItemSelected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch(menuItem.getItemId()){
                case R.id.navigation_offers:
                    selectedFragment = new OffersFeedFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new DashboardFragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new NotificationsFragment();
                    break;
                case R.id.navigation_settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();
            return true;
        }
   };
}
