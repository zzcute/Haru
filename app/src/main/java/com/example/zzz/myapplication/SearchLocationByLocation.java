package com.example.zzz.myapplication;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.zzz.myapplication.R.id.map;

public class SearchLocationByLocation extends AppCompatActivity
        implements  OnMapReadyCallback, GoogleMap.OnMapClickListener{

    ImageView imgView;
    static final String TAG = "MainActivity";

    ImageView logoImage;

    TextView mTextMessage;
    ImageGallery mComplexGallery;

    double myLati = 37.56;
    double myLongi = 126.97;

    GoogleMap mGoogleMap;


    private ArrayList<LatLng> arrayPoint;

    LocationManager lm = null;

    String mRoot = "";
    String mPath = "";
    String mDCIM = "";
    TextView mTextMsg;
    ListView mListFile;
    ArrayList<String> mArFile;

    String loadDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scene_with_login);

        mTextMessage=(TextView)findViewById(R.id.textMessage);
        mComplexGallery=(ImageGallery)findViewById(R.id.imageGallery1);

        getDateString();

        FragmentManager fragmentManager = getFragmentManager();
        final MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        arrayPoint = new ArrayList<LatLng>();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        Log.d("Exit", "isExit?");

    }



    public void savePosition()
    {
        SharedPreferences pref = getSharedPreferences("Position", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        String date = getDateString();

        int size = arrayPoint.size();

        Log.d("saveSize", String.valueOf(size));

        editor.putInt(date + "size", size);

        for(int i = 0; i < size; i++) {
            editor.putFloat(date + "lati" + i, (float)arrayPoint.get(i).latitude);
            editor.putFloat(date + "loggi" + i, (float)arrayPoint.get(i).longitude);
        }

        editor.commit();
        //Gson gson = new Gson();

    }

    public void loadPosition()
    {
        String date = getDateString();

        SharedPreferences pref = getSharedPreferences("Position", Context.MODE_PRIVATE);

        int size = pref.getInt(date + "size", 0);

        Log.d("size", String.valueOf(size));

        if(size == 0)
            return;

        arrayPoint.clear();

        for(int i = 0; i < size; i++)
        {
            double lati = (double)pref.getFloat(date + "lati" + i, 0);
            double longi = (double)pref.getFloat(date + "longi" + i, 0);

            LatLng latLng = new LatLng(lati, longi);

            arrayPoint.add(latLng);
        }

        PolylineOptions polylineOptions = new PolylineOptions();

        polylineOptions.addAll(arrayPoint);

        mGoogleMap.clear();
        mGoogleMap.addPolyline(polylineOptions);
    }


    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String str_date = df.format(new Date());

        Log.d("tag", str_date);

        return str_date;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);

        marker.title("Test");
        mGoogleMap.addMarker(marker);

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        mGoogleMap = map;

        LatLng myPosition = new LatLng(myLati, myLongi);

        //map.addMarker(new MarkerOptions().position(myPosition).title("Hi"));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        loadPosition();

        //setShowPictureButton();
        //사진 불러오기
        getPictureFromSD();
    }

    public void getPictureFromSD()
    {
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mTextMsg.setText(mRoot);
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        File[] pics = dcim.listFiles();


        String[] fileList = getFileList(dcimPath);

        for(int i = 0; i < fileList.length; i++)
        {
            try {


                ExifInterface exif = new ExifInterface(dcimPath + "/" + fileList[i]);

                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);


                if(latitude == null) {

                }

                Log.d("lenth",String.valueOf(i));

                float latitudeInt = convertToDegree(latitude);
                float longitudeInt = convertToDegree(longitude);


                Bitmap bmp = BitmapFactory.decodeFile(dcimPath + "/" + fileList[i]);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 84, 84, false);

                Canvas canvas = new Canvas(smallMarker);

                mComplexGallery.draw(canvas);


                //bmp.setHeight(1);
                //bmp.setWidth(1);

                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeInt, longitudeInt)).title("Test"))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeInt, longitudeInt)));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public String[] getFileList(String strPath){

        File fileRoot = new File(strPath);

        if(fileRoot.isDirectory() == false)
            return null;

        Log.d("tag", "Is it?");

        mPath = strPath;
        //mTextMsg.setText(mPath);

        FilenameFilter filter = new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".jpg");
            }
        };

        String[] fileList = fileRoot.list(filter);

        return fileList;
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (exif.getAttribute(tag));
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
}
