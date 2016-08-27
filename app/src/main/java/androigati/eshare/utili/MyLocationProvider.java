package androigati.eshare.utili;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Chai on 27/08/2016.
 */
public class MyLocationProvider implements LocationListener {


    private String locProvider;
    private GoogleMap mapController;
    private LocationManager locationManager;
    private Activity context;
    private Location currentLocation;
    private InotifyMapActivity notifier;

    public MyLocationProvider(Activity context, InotifyMapActivity notifier) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locProvider = LocationManager.GPS_PROVIDER;//default
        this.context = context;
        this.notifier = notifier;
    }

    public void changeLocationProvider(String lProvider) {
        this.locProvider = lProvider;
    }

    public boolean startRegisterLocation() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                locationManager.requestLocationUpdates(locProvider, 0, 0, this);
                return true;
            }
        }
        return false;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        //mapController.addMarker(new MarkerOptions().position(pos).title("Marker in Sydney"));
        //mapController.moveCamera(CameraUpdateFactory.newLatLng(pos));
        notifier.notifyLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setMapController(GoogleMap mapController) {
        this.mapController = mapController;
    }


}
