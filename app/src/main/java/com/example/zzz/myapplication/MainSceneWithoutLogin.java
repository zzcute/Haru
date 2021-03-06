package com.example.zzz.myapplication;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.zzz.myapplication.R.id.map;

public class MainSceneWithoutLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnTouchListener {

    TextView mTextMessage;
    ImageGallery mComplexGallery;

    ToggleButton tb;

    double myLati = 37.56;
    double myLongi = 126.97;

    GoogleMap mGoogleMap;
    float zoomLevel = 15;

    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoint;

    LocationManager lm = null;

    String mRoot = "";
    String mPath = "";
    String mDCIM = "";
    TextView mTextMsg;
    ListView mListFile;
    ArrayList<String> mArFile;

    String loadDate;

    String todayExif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scene_without_login);

        todayExif = getDateStringForExif();

        Log.d("tagDay", todayExif);

        mTextMessage=(TextView)findViewById(R.id.textMessage);
        mComplexGallery=(ImageGallery)findViewById(R.id.imageGallery1);

        getAcessForSave();
        getAcessForLocation();

        setNavMenu();

        setToggleButton();

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

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {

            Log.d("tag2","mComplexGallery.fileName");

        }

        return true;
    }


   /* public void setShowPictureButton()
    {
        Button button = (Button)findViewById(R.id.mapButton);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mGoogleMap.clear();
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.argb((int)(255 * 0.5), 46, 43, 61));
                polylineOptions.width(10);
                polylineOptions.addAll(arrayPoint);
                mGoogleMap.addPolyline(polylineOptions);
                getPictureFromSD();
                savePosition();
            }
        });
    }*/

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

    public void saveFile()
    {
        try {
            String FileName = "SaveFile" + getDateString();
            FileOutputStream os = openFileOutput(FileName, MODE_PRIVATE);

            String saveList = arrayPoint.toString();

            os.write(saveList.getBytes());
            os.close();

        }
        catch(IOException e) {

        }
    }

    public void LoadFile()
    {
        try{
            String FileName = "SaveFile" + getDateString();
            FileInputStream os = openFileInput(FileName);

            InputStreamReader inputStreamReader = new InputStreamReader(os);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            //Byte a = os.read();

        }
        catch (IOException e){

        }
    }

    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String str_date = df.format(new Date());


        return str_date;
    }

    public String getDateStringForExif()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }

    public void setNavMenu()
    {
        ImageButton listButton = (ImageButton)findViewById(R.id.listButton);

        listButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                drawer.openDrawer(Gravity.LEFT);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void setToggleButton()
    {
        tb = (ToggleButton) findViewById(R.id.toggleButton);
//        text = (TextView) findViewById(R.id.gpsState);
        //      testText = (TextView)findViewById(R.id.testText);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(tb.isChecked()) {

                        tb.setBackgroundResource(R.mipmap.mylocation_ing);

                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                1,
                                100,
                                mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                1,
                                100,
                                mLocationListener);

                    }
                    else{
                        tb.setBackgroundResource(R.mipmap.mylocation);
                        lm.removeUpdates(mLocationListener);
                    }

                }
                catch (SecurityException ex) {

                }

            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_scene_without_login, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent intent = new Intent(
                    getApplicationContext(), // 현재 화면의 제어권자
                    LoginActivity.class); // 다음 넘어갈 클래스 지정
            startActivity(intent);


        } else if (id == R.id.nav_register) {
            Intent intent = new Intent(
                    getApplicationContext(), // 현재 화면의 제어권자
                    RegisterActivity.class); // 다음 넘어갈 클래스 지정
            startActivity(intent);
        }
//        else if(id == R.id.nav_test){
//            Intent intent = new Intent(
//                    getApplicationContext(), // 현재 화면의 제어권자
//                    MainSceneWithLogin.class); // 다음 넘어갈 클래스 지정
//            startActivity(intent);
//        } else if(id == R.id.nav_calender) {
//            Intent intent = new Intent(
//                    getApplicationContext(), // 현재 화면의 제어권자
//                    CalendarViewExample.class); // 다음 넘어갈 클래스 지정
//            startActivity(intent);
//        } else if(id == R.id.nav_test){
//            Intent intent = new Intent(
//                    getApplicationContext(),
//                    SearchLocationByKeyword.class);
//            startActivity(intent);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getAcessForSave()
    {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
        {
            int permisionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permisionResult == PackageManager.PERMISSION_DENIED)
            {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    //AlertDialog.Builder a;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainSceneWithoutLogin.this);

                    dialog.setTitle("권한이 필요합니다")
                            .setMessage("이 기능을 사용하기 위해서 저장 권한이 필요합니다")
                            .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    {
                                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    Toast.makeText(MainSceneWithoutLogin.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }
                else
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                }
            }

        }
    }

    public void getAcessForLocation()
    {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
        {
            int permisionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if(permisionResult == PackageManager.PERMISSION_DENIED)
            {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    //AlertDialog.Builder a;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainSceneWithoutLogin.this);

                    dialog.setTitle("권한이 필요합니다")
                            .setMessage("이 기능을 사용하기 위해서 위치 권한이 필요합니다")
                            .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    {
                                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    Toast.makeText(MainSceneWithoutLogin.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();



                }
                else
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000 :

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가

// 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
// 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        tb.setText("클릭");

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);

        marker.title("Test");
        mGoogleMap.addMarker(marker);

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        mGoogleMap = map;
        mComplexGallery.googleMap = map;
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

        dcimPath += "/Camera";


        String[] fileList = getFileList(dcimPath);

        if(fileList == null)
        {
            return;
        }

        Log.d("tag", String.valueOf(fileList.length));

        for(int i = 0; i < fileList.length; i++)
        {
            try {
                ExifInterface exif = new ExifInterface(dcimPath + "/" + fileList[i]);

                String date = getTagString(ExifInterface.TAG_DATETIME, exif);

                if(date == null)
                    continue;

                int idx = date.indexOf(" ");

                String date2 = date.substring(0, idx);

                if(date2 != todayExif)
                {
                   //continue;
                }
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);

                Log.d("tag",String.valueOf(i));

                if(latitude == null) {
                    continue;
                }

                Log.d("tag",String.valueOf(i));
                float latitudeInt = convertToDegree(latitude);
                float longitudeInt = convertToDegree(longitude);

                Bitmap bmp = BitmapFactory.decodeFile(dcimPath + "/" + fileList[i]);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 84, 84, false);

                mComplexGallery.addImage(smallMarker, fileList[i]);

                LatLng markerPosition = new LatLng(latitudeInt, longitudeInt);
                mComplexGallery.list.add(markerPosition);
                mGoogleMap.addMarker(new MarkerOptions().position(markerPosition).title(fileList[i]))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeInt, longitudeInt)));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void ReadSDCard(){
        List<String> tFileList = new ArrayList<String>();
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/");

        File[] files = f.listFiles();

        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];

            String curFile = file.getPath();
            //String ext = curFile.substring(curFile, )
        }
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            //
            // testText.setText(String.valueOf(longitude));

            LatLng myPosition = new LatLng(latitude, longitude);

            if(arrayPoint.size() == 0)
            {
                mGoogleMap.addMarker(new MarkerOptions().position(myPosition)).setTitle("Start");
            }

            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.argb((int)(255 * 0.5), 46, 43, 61));
            polylineOptions.width(10);

            arrayPoint.add(myPosition);


            polylineOptions.addAll(arrayPoint);
            mGoogleMap.addPolyline(polylineOptions);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            //   text.setText("수신중");
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            //  text.setText("수신완료");
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            //     text.setText("위치변경");
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };


    public boolean isSdCard(){

        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(this, "SD Card dose not exist",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
