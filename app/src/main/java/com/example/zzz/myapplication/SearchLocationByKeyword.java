package com.example.zzz.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class SearchLocationByKeyword  extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    GoogleMap mGoogleMap;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    ImageGalleryForSearch mComplexGallery;

    LatLng currentPostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location_by_keyword);

        Log.d("Tag2", "Create?");
        mComplexGallery=(ImageGalleryForSearch)findViewById(R.id.imageGallery1);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Log.d("Tag3", "OnMap?");

        mGoogleMap = map;

        LatLng position = new LatLng(126.97, 37.56);

        mGoogleMap.addMarker(new MarkerOptions().position(position).title("marker"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Tag5", "Why?");
                currentPostion = place.getLatLng();
                mGoogleMap.addMarker(new MarkerOptions().position(currentPostion).title("marker"));

                PolylineOptions options = new PolylineOptions();
                float radius = 0.02f;
                int numPoints = 100;
                double phase = 2 * Math.PI / numPoints;
                for (int i = 0; i <= numPoints; i++) {
                    options.add(new LatLng(currentPostion.latitude + radius * Math.sin(i * phase),
                            currentPostion.longitude + radius * Math.cos(i * phase)));
                }
                int color = Color.argb(255, 75, 69, 101);
                map.addPolyline(options
                        .color(color)
                        .width(8));

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPostion));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(1));

                getPictureFromSD();
            }

            @Override
            public void onError(Status status) {
                Log.d("Tag4", "An error occurred: " + status);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                //Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                // Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    private void callPlaceSearchIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    public void getPictureFromSD()
    {
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        dcimPath += "/Camera";

        String[] fileList = getFileList(dcimPath);

        if(fileList == null)
        {
            return;
        }

        for(int i = 0; i < fileList.length; i++)
        {
            try {
                ExifInterface exif = new ExifInterface(dcimPath + "/" + fileList[i]);

                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);

                if(date == null)
                    continue;

                int idx = date.indexOf(" ");

                String date2 = date.substring(0, idx);

                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);


                if(latitude == null) {
                    continue;
                }

                Log.d("tag",String.valueOf(i));
                double latitudeDouble = convertToDegree(latitude);
                double longitudeDouble = convertToDegree(longitude);

                double distance = calDistance(currentPostion.latitude, currentPostion.longitude, latitudeDouble,  longitudeDouble);

                if(distance < 3000)
                {
                    Bitmap bmp = BitmapFactory.decodeFile(dcimPath + "/" + fileList[i]);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 84, 84, false);

                    mComplexGallery.addImage(smallMarker, fileList[i]);

                    LatLng markerPosition = new LatLng(latitudeDouble, longitudeDouble);
                    mComplexGallery.list.add(markerPosition);
                    mGoogleMap.addMarker(new MarkerOptions().position(markerPosition).title(fileList[i]))
                            .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeDouble, longitudeDouble)));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }

                else
                {
                    continue;
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            }

        }
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    };

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }


    public String[] getFileList(String strPath){

        File fileRoot = new File(strPath);

        if(fileRoot.isDirectory() == false)
            return null;

        FilenameFilter filter = new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".jpg");
            }
        };

        String[] fileList = fileRoot.list(filter);

        return fileList;
    }
}
