package androidgati.eshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import androigati.eshare.utili.InotifyMapActivity;
import androigati.eshare.utili.MyLocationProvider;
import androigati.eshare.utili.NoticeDialogFragment;
import de.rwth.R;
import de.rwth.setups.MyArSetup;
import system.ArActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NoticeDialogFragment.NoticeDialogListener, InotifyMapActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_GPS_LOCATION = 200;
    private GoogleMap mMap;
    private MyLocationProvider locationprovider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationprovider = new MyLocationProvider(MapsActivity.this, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
        locationprovider.setMapController(mMap);

    }

    @Override
    public void onResume() {
        super.onResume();
        askPermission();
        if (!CheckEnableGPS()) {
            showAlertDialog();
        }
    }

    private void askPermission() {
        if (locationprovider.startRegisterLocation() == false) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_GPS_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_GPS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    askPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean CheckEnableGPS() {
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {

            return false;
        } else
            return true;
    }

    private void showAlertDialog() {
        NoticeDialogFragment dialogFragment = new NoticeDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "alert gps dialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(onGPS);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        showAlertDialog();
    }

    private boolean updateReceived = false;

    @Override
    public void notifyLocationChanged(Location newlocation) {

        if (updateReceived == false) {
            MyArSetup customSetup = new MyArSetup(locationprovider);
            List<Location> locs = new ArrayList<>();
            Location further = new Location(newlocation);
            further.setLongitude(newlocation.getLongitude()-0.00001d);
            further.setLatitude(newlocation.getLatitude()-0.00001d);
            locs.add(further);
            customSetup.setLocations(locs);
            ArActivity.startWithSetup(this, customSetup);
            updateReceived = true;
        }
    }
}
