package com.lydia.meet_me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.service.carrier.CarrierMessagingService;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.location.LocationManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleMap mMap;
    private Marker meet;
    private double lat4;
    private double lon4;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private String friend_location;
    private String message = null;
    protected Button button;
    public Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;
    private double search =0;
    public Bitmap bitmap = null;
    public static final String TAG = MapsActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.lydia.meet_me.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.lydia.meet_me.MESSAGE1";
    public final static String EXTRA_MESSAGE2 = "com.lydia.meet_me.MESSAGE2";
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        friend_location = intent.getStringExtra(LaunchActivity.EXTRA_MESSAGE);
        placeMarkers = new Marker[MAX_PLACES];
        lat4 = intent.getDoubleExtra("newlat", 0);
        lon4 = intent.getDoubleExtra("newlon",0);



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        setUpMapIfNeeded();

        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());

        Context context = getApplicationContext();
        CharSequence text = "Swipe right for Street View!!!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void getStreet(View view){
        final ImageButton button65 = (ImageButton)findViewById(R.id.streetview);
        button65.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, StreetViewActivity.class);
                i.putExtra(EXTRA_MESSAGE1, lat4);
                i.putExtra(EXTRA_MESSAGE2, lon4);
                startActivity(i);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    private void setUpMap(){

        mMap.setMyLocationEnabled(true);
        mGoogleApiClient.connect();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void onSearch()
    {
        //EditText location_tf= (EditText)findViewById(R.id.TFaddress);
        //String location= location_tf.getText().toString();

        String location = friend_location;
        /*
        List<Address> addressList=null;
        if(location!=null|| !location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address= addressList.get(0);
            LatLng latLng= new LatLng(address.getLatitude(),address.getLongitude());
           // mMap.addMarker(new MarkerOptions().position(latLng).title("Friend Marker"));
           */
        LatLng latLng = new LatLng(lat4,lon4);
        Marker friend = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .alpha(0.7f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_archer))
                .draggable(true)
                .title("My friend/pal/buddy"));

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        double lat1= mLastLocation.getLatitude();
        double lon1= mLastLocation.getLongitude();
        // double lat2= address.getLatitude();
        //double lon2= address.getLongitude();
        double lat2= lat4;
        double lon2= lon4;

        System.out.println("lat1="+lat1+"lon1 = "+lon1+"lat2 = "+lat2+"lon2 = "+lon2);

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        lat4 = Math.toDegrees(lat3);
        lon4 = Math.toDegrees(lon3);
        LatLng latLnga= new LatLng(lat4, lon4);



        meet = mMap.addMarker(new MarkerOptions().position(latLnga).title("Meeting Place")
                .draggable(true).alpha(.7f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_meetloc)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLnga));


        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/"+
                "json?location="+lat4+","+lon4+
                "&radius=1000&sensor=true"+"&types=food%7Cbar"+
                "&key=AIzaSyA_os0YpUO-LdmsyMjzZY5jFrqHjO8bpDc";
        Log.d("SEASTR", placesSearchStr);
        search=1;
        new DownloadFiles(this).execute(lat4, lon4, search);





        message = locationfind(lat4, lon4);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng loc= marker.getPosition();
                Log.d("LOCATION",loc.toString());
                if(marker.getTitle().equals("Meeting Place")) {
                    message = locationfind(loc.latitude, loc.longitude);
                    marker.setSnippet("New Location");
                    search =1;
                    new DownloadFiles(MapsActivity.this).execute(marker.getPosition().latitude, marker.getPosition().longitude,search);

                }
            }
        });

        mMap.setOnMarkerClickListener(new OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                URL imageURL = null;
                try {
                    imageURL = new URL("https://maps.googleapis.com/maps/api/streetview?size=400x400&location="+marker.getPosition().latitude+","+marker.getPosition().longitude+"&fov=90&heading=235&pitch=10&key=AIzaSyA_os0YpUO-LdmsyMjzZY5jFrqHjO8bpDc");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                lon4=marker.getPosition().longitude;
                lat4=marker.getPosition().latitude;

                Log.d("IC URL", imageURL.toString());

                new DownloadImageTask().execute(imageURL.toString());

                return false;
            }
        });


        final Intent intent;
        intent = new Intent(this, display.class);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra(EXTRA_MESSAGE, message);

                startActivity(intent);
            }
        });


    }



    protected void onResume(){
        super.onResume();
        setUpMapIfNeeded();
    }
    //geocoder for adress from Latlng
    private String locationfind(double lat4,double lon4){
        List<Address> geocodeMatches = null;
        String Address1 = null;
        String Address2 = null;
        String State = null;
        String Zipcode = null;
        String Country = null;

        try {
            geocodeMatches =
                    new Geocoder(this).getFromLocation(lat4, lon4, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Location not recognized, try again.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
            // create alert dialog

        }

        if (!geocodeMatches.isEmpty()) {
            Address1 = geocodeMatches.get(0).getAddressLine(0);
            Address2 = geocodeMatches.get(0).getAddressLine(1);
            State = geocodeMatches.get(0).getAdminArea();
            Zipcode = geocodeMatches.get(0).getPostalCode();
            Country = geocodeMatches.get(0).getCountryName();
        }

        System.out.println("Address is " + Address1 + Address2 + State + Zipcode + Country);


        if (Address1 != null) {
            message = Address1;
        }
        if (Address2 != null) {
            message = message + " " + Address2;
        }
        if (State != null) {
            message = message + " " + State;
        }
        if (Zipcode != null) {
            message = message + " " + Zipcode;
        }
        if (Country != null) {
            message = message + " " + Country;
        }

        return message;
    }


    public void setUpMapIfNeeded(){
        if (mMap==null){
            mMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if(mMap !=null){
                setUpMap();
            }
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if (event2.getX() < event1.getX()) {
                Intent i = new Intent(MapsActivity.this, StreetViewActivity.class);
                i.putExtra(EXTRA_MESSAGE1, lat4);
                i.putExtra(EXTRA_MESSAGE2, lon4);
                startActivity(i);
            }

            return true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnection");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng latLnga= new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLnga).title("Me")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_doge)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLnga, 15));
        }
        onSearch();
    }


    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("ConnectionFailed");
    }

    public void onMarkerDragEnd (Marker meet){
        LatLng loc= meet.getPosition();
        Log.d("LOC",loc.toString());
        message = locationfind(loc.latitude,loc.longitude);
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            //set image of your imageview
            //roundedImage = new RoundImage(result);
            //imageView1.setBackground(roundedImage);
            Toast.makeText(getApplicationContext(), "Image Loaded",
                    Toast.LENGTH_SHORT).show();
            int bs = bitmap.getPixel(0,0);
            ImageButton street = (ImageButton) findViewById(R.id.streetview);
            if(bs!= -1776674) {
                Log.d("bitmapSize", Integer.toString(bs));

                street.setImageBitmap(bitmap);
            }
            else{
                street.setImageBitmap(null);
            }
        }
    }


}
