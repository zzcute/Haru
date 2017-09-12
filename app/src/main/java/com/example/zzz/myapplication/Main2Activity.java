package com.example.zzz.myapplication;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.zzz.myapplication.R.id.map;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    ToggleButton tb;
    // TextView text;
    // TextView testText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
         //       this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

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

        getAcessForLocation();

        tb = (ToggleButton) findViewById(R.id.toggleButton);
//        text = (TextView) findViewById(R.id.gpsState);
        //      testText = (TextView)findViewById(R.id.testText);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_READ_CONTEXT);

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(tb.isChecked()) {

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
                        lm.removeUpdates(mLocationListener);
                    }

                }
                catch (SecurityException ex) {

                }

            }

        });

        FragmentManager fragmentManager = getFragmentManager();
        final MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


       /*Button plusButton = (Button)findViewById(R.id.plusButton);

        plusButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(++zoomLevel);
                mGoogleMap.animateCamera(zoom);
            }
        });

      //  Button minusButton = (Button)findViewById(R.id.minusButton);

        minusButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(--zoomLevel);
                mGoogleMap.animateCamera(zoom);
            }
        });*/

        arrayPoint = new ArrayList<LatLng>();
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
        getMenuInflater().inflate(R.menu.main2, menu);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Main2Activity.this);

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
                                    Toast.makeText(Main2Activity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
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

        LatLng myPosition = new LatLng(myLati, myLongi);

        //map.addMarker(new MarkerOptions().position(myPosition).title("Hi"));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        //사진 불러오기

        if(isSdCard() == false)
            finish();

        //mTextMsg = (TextView)findViewById(R.id.textMessage);

//        testText = (TextView)findViewById(R.id.testText);

        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mTextMsg.setText(mRoot);
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        //dcimPath += "/Camera";

        File[] pics = dcim.listFiles();

        /*if(pics != null){
            for(File pic : pics)
            {
                testText.setText(pic.getName());
            }
        }*/

        String[] fileList = getFileList(dcimPath);

        //      int a = fileList.length;

//        String b = String.valueOf(a);

//        testText.setText(b);

        Log.d("FileLength", "??");

        Log.d("로그", "로oo그");

        //

        for(int i = 0; i < fileList.length; i++)
        {
            try {

//                testText.setText(dcimPath + "/" + fileList[i]);

                ExifInterface exif = new ExifInterface(dcimPath + "/" + fileList[i]);

                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);


               /* TextView lati = (TextView)findViewById(R.id.lati);
                lati.setText(latitude);
                TextView longi = (TextView)findViewById(R.id.longi);
                longi.setText(longitude);*/

                if(latitude == null) {
                    //    longi.setText("what the hell");

                    continue;
                }

                Bitmap bMap = BitmapFactory.decodeFile(dcimPath + "/" + fileList[i]);

                float latitudeInt = convertToDegree(latitude);
                float longitudeInt = convertToDegree(longitude);


                Bitmap bmp = BitmapFactory.decodeFile(dcimPath + "/" + fileList[i]);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 84, 84, false);


                //bmp.setHeight(1);
                //bmp.setWidth(1);

                map.addMarker(new MarkerOptions().position(new LatLng(latitudeInt, longitudeInt)).title("Test"))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeInt, longitudeInt)));

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
