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
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import androigati.eshare.access.AccessManager;
import androigati.eshare.adapter.ContentRecyclerViewAdapter;
import androigati.eshare.model.Content;
import androigati.eshare.utils.DimensionHelper;
import androigati.eshare.utils.InotifyMapActivity;
import androigati.eshare.utils.MyLocationProvider;
import androigati.eshare.utils.NoticeDialogFragment;
import androigati.eshare.widget.ImageDialog;
import androigati.eshare.widget.TextDialog;
import de.rwth.R;
import de.rwth.setups.EShareSetup;
import system.ArActivity;

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
                .findFragmentById(R.id.map_content_fragment);
        mapFragment.getMapAsync(this);
        locationProvider = new MyLocationProvider(MapsActivity.this, this);
        Switch arSwitch = (Switch) findViewById(R.id.ar_switch);
    }

    @Override
    public void onResume() {
        super.onResume();
        askPermission();
        if (!checkEnableGPS()) {
            showAlertDialog();
        }
        Switch arSwitch = (Switch) findViewById(R.id.ar_switch);
        arSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EShareSetup customSetup = new EShareSetup();
                            ArActivity.startWithSetup(MapsActivity.this, customSetup);
                        }
                    }, 500);
                }
            }
        });
        arSwitch.setChecked(false);
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

        addContent();
    }

    private void addContent() {

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

                    OnContentMarkerClickListener markerClickListener = new OnContentMarkerClickListener(
                            MapsActivity.this,
                            contentList);
                    mMap.setOnMarkerClickListener(markerClickListener);

                    for (Content content : contentList) {
                        LatLng pos = new LatLng(
                                content.getPosition().getLat(),
                                content.getPosition().getLng());
                        Marker contentMarker = null;

                        switch (content.getType()) {

                            case "image":
                                contentMarker = mMap.addMarker(new MarkerOptions()
                                        .position(pos)
                                        .icon(BitmapDescriptorFactory.fromBitmap(imageMarker))
                                        .title(content.getTitle()));
                                break;
                            case "text":
                                contentMarker = mMap.addMarker(new MarkerOptions()
                                        .position(pos)
                                        .icon(BitmapDescriptorFactory.fromBitmap(textMarker))
                                        .title(content.getTitle()));
                                break;
                        }

                        if (contentMarker != null)
                            markerClickListener.addMarker(contentMarker);

                        ContentRecyclerViewAdapter contentRecyclerViewAdapter =
                                new ContentRecyclerViewAdapter(contentList);
                        RecyclerView contentRecyclerView = (RecyclerView) findViewById(R.id.content_recycler_view);
                        contentRecyclerView.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
                        contentRecyclerView.setAdapter(contentRecyclerViewAdapter);
                    }
                }
            }
        }.execute();
    }

    public void switchView(View v) {
        ImageView switchViewButton = (ImageView) v;
        View mapView = findViewById(R.id.map_content_fragment);
        if (mapView.getVisibility() == View.VISIBLE) {
            mapView.setVisibility(View.INVISIBLE);
            switchViewButton.setImageResource(R.drawable.icon_map);
        } else {
            mapView.setVisibility(View.VISIBLE);
            switchViewButton.setImageResource(R.drawable.icon_list);
        }
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
    public void notifyLocationChanged(Location newLocation) {

        if (!updateReceived) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    newLocation.getLatitude(),
                    newLocation.getLongitude())
            ));
            updateReceived = true;
        }
    }

    public class OnContentMarkerClickListener implements GoogleMap.OnMarkerClickListener {

        private Context context;
        private List<Content> contentList;
        private List<Marker> markerList;

        public OnContentMarkerClickListener(Context context, List<Content> contentList) {
            this.context = context;
            this.contentList = contentList;
            this.markerList = new ArrayList<>();
        }

        public void addMarker(Marker marker) {
            markerList.add(marker);
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            int i = 0;
            for (Marker contentMarker : markerList) {
                if (marker.equals(contentMarker)) {
                    Content content = contentList.get(i);
                    switch (content.getType()) {
                        case "image":
                            ImageDialog imageDialog = new ImageDialog(context, content);
                            imageDialog.show();
                            break;
                        case "text":
                            TextDialog textDialog = new TextDialog(context, content);
                            textDialog.show();
                            break;
                    }
                    return true;
                }
                i++;
            }
            return false;
        }
    }
}
