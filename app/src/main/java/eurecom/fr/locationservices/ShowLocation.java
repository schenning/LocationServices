package eurecom.fr.locationservices;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocation extends AppCompatActivity  implements LocationListener, OnMapReadyCallback {
    protected LocationManager locationManager = null;
    private String provider;
    Location location;
    TextView latitudeField;
    TextView longitudeField;
    public static final int MY_PERMISSIONS_LOCATION = 0;
    private GoogleMap googleMap;
    static final LatLng NICE = new LatLng(43.7031, 7.02661);
    static final LatLng EURECOM = new LatLng(43.6143899, 7.0689363);

    @Override
    public void onMapReady(GoogleMap Map) {
            googleMap = Map;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(NICE)
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();
            googleMap.addMarker(new MarkerOptions()
                    .position(NICE)
                    .title("Nice")
                    .snippet("Enjoy French Riviera"));
            googleMap.addMarker(new MarkerOptions()
                    .position(EURECOM)
                    .title("EURECOM")
                    .snippet("ENJOY STUDY!"));
        }

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permission: ", "To be checked");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_LOCATION);
            return;
        } else
            Log.i("Permission: ", "GRANTED");
        latitudeField = (TextView) findViewById(R.id.textView2);
        longitudeField = (TextView) findViewById(R.id.textView);
        location = locationManager.getLastKnownLocation(provider);
        if (locationManager == null)
            locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Log.i("GPS", "not enabled");
// Build an alert dialog here that requests the user
// to enable location services when he clicks over "ok"
            enableLocationSettings();
        } else {
            Log.i("GPS", "enabled");
        }
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Access:", "Now permissions are granted");
                    // permission was granted, yay!
                } else {
                    Log.i("Access:", " permissions are denied");
                    //disable the functionality that depends on this permission.
                }
                break;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }


    public void showLocation(View view){
        Log.i("showLocation", "Entered");
        switch (view.getId()){
            case R.id.button:
                updateLocationView();
        }
    }
    public void updateLocationView(){
        if (location != null){
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latitudeField.setText(String.valueOf(lat));
            longitudeField.setText(String.valueOf(lng));
        } else{
            Log.i("showLocation","NULL");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location","LOCATION CHANGED!!!");
        updateLocationView();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

}




