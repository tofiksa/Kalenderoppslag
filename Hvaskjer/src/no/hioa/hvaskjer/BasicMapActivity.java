package no.hioa.hvaskjer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */
public class BasicMapActivity extends Activity implements OnClickListener, LocationListener {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 1 * 60 * 1000; //1 minute
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    double lat = 0;
    double lng = 0;
    private String provider;
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
			Intent i = new Intent(getApplicationContext(),HvaSkjer.class);
			startActivity(i);
		} else if (item.getItemId() == R.id.menu_refresh) {
			Toast.makeText(this, "this.lat "+this.lat, Toast.LENGTH_SHORT).show();
			getWindow().getDecorView().postDelayed(
			        new Runnable() {
			            @Override
			            public void run() {
			            	setUpMapIfNeeded();
			            }
			        }, 1000);
		} else if (item.getItemId() == R.id.menu_search) {
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.menu_share) {
			Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
		}
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	setContentView(R.layout.basic_demo);
    	mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
    			.getMap();
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
    	service.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
    	boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    	
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Toast.makeText(this, "Selected Provider " + provider,
                    Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {

            //do something
        }
        
        //setUpMapIfNeeded();
    }
    
    @Override
    protected void onPause() {
          super.onPause();
          locationManager.removeUpdates(this);
    }
    
    @Override
	public void onLocationChanged(Location location) {
    	this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        
        Toast.makeText(this, "Location " + lat+","+lng,
                Toast.LENGTH_LONG).show();
        LatLng coordinate = new LatLng(lat, lng);
        Toast.makeText(this, "Location " + coordinate.latitude+","+coordinate.longitude,
                Toast.LENGTH_LONG).show();
        Marker startPerc = mMap.addMarker(new MarkerOptions()
        .position(coordinate).title("Her er du"));
        
        
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
	}
    
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    @Override
	public void onProviderDisabled(String provider) {
    	Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
	}
    
    @Override
	public void onProviderEnabled(String provider) {
    	Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();

	}

    
    @Override
	public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
        //setUpMapIfNeeded();
    }

   
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	
        	mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        			.getMap();
        	setUpMapIfNeeded();
        }
        
        if (mMap != null && this.lat != 0) {
    		setUpMap();
    	}
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(this.lat, this.lng)).title("Marker"));
    }


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
    

	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
	}
}