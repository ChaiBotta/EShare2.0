package androigati.eshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androigati.eshare.access.AccessManager;
import androigati.eshare.model.Content;
import androigati.eshare.utils.DimensionHelper;
import androigati.eshare.utils.InotifyMapActivity;
import androigati.eshare.utils.MyLocationProvider;
import androigati.eshare.utils.NoticeDialogFragment;
import androigati.eshare.widget.ImageDialog;
import de.rwth.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NoticeDialogFragment.NoticeDialogListener, InotifyMapActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_GPS_LOCATION = 200;
    private GoogleMap mMap;
    private MyLocationProvider locationProvider;
    private boolean updateReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProvider = new MyLocationProvider(MapsActivity.this, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        askPermission();
        if (!checkEnableGPS()) {
            showAlertDialog();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
        locationProvider.setMapController(mMap);

        addContentOnMap();
    }

    private void addContentOnMap() {

        new AsyncTask<Void, Void, Boolean>() {

            List<Content> contentList;
            Bitmap imageMarker, textMarker;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    contentList = AccessManager.getNearbyContent(null);
                } catch (Exception e) {
                    return false;
                }
                imageMarker = BitmapFactory.decodeResource(getResources(),
                        R.drawable.image_marker);
                textMarker = BitmapFactory.decodeResource(getResources(),
                        R.drawable.text_marker);
                imageMarker = Bitmap.createScaledBitmap(
                        imageMarker,
                        DimensionHelper.dpToPx(30),
                        DimensionHelper.dpToPx(30),
                        false);
                textMarker = Bitmap.createScaledBitmap(
                        textMarker,
                        DimensionHelper.dpToPx(30),
                        DimensionHelper.dpToPx(30),
                        false);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                if (!successful)
                    Toast.makeText(MapsActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                else if (!contentList.isEmpty()) {
                    for (Content content : contentList) {
                        LatLng pos = new LatLng(
                                content.getPosition().getLat(),
                                content.getPosition().getLng());

                        Marker contentMarker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .icon(BitmapDescriptorFactory.fromBitmap(imageMarker))
                                .title(content.getTitle()));


                        mMap.setOnMarkerClickListener(new OnContentMarkerClickListener(
                                MapsActivity.this,
                                contentMarker,
                                content));

                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    }
                }
            }
        }.execute();
    }

    private void askPermission() {
        if (!locationProvider.startRegisterLocation()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);

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

    private boolean checkEnableGPS() {
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

    @Override
    public void notifyLocationChanged(Location newlocation) {

        /*
        if (updateReceived == false) {
            MyArSetup customSetup = new MyArSetup(locationProvider);
            List<Location> locs = new ArrayList<>();
            Location further = new Location(newlocation);
            further.setLongitude(newlocation.getLongitude() - 0.00001d);
            further.setLatitude(newlocation.getLatitude() - 0.00001d);
            locs.add(further);
            customSetup.setLocations(locs);
            ArActivity.startWithSetup(this, customSetup);
            updateReceived = true;
        }*/
    }

    public class OnContentMarkerClickListener implements GoogleMap.OnMarkerClickListener {

        private Context context;
        private Content content;
        private Marker contentMarker;

        public OnContentMarkerClickListener(Context context, Marker contentMarker, Content content) {
            this.context = context;
            this.contentMarker = contentMarker;
            this.content = content;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.equals(contentMarker)) {
                switch (content.getType()) {
                    case "image":
                        ImageDialog imageDialog = new ImageDialog(context, content);
                        imageDialog.show();
                        break;
                }
                return true;
            } else
                return false;
        }
    }
}
