package com.example.zzz.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

//
//import RelativeLayout;
/**
 * Created by psk93 on 2017-09-16.
 */

public class ImageGallery extends RelativeLayout implements View.OnTouchListener{
    Context mContext;
    ImageView mImageMain;
    LinearLayout mScrollLayout1;
    ArrayList<ImageView> arImageView = new ArrayList<ImageView>();
    ArrayList<Integer> arImageRes = new ArrayList<Integer>();

    public ImageGallery(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext=context;

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_image_gallery, this, true);

        //mImageMain =(ImageView) findViewById(R.id.imageMain);
        mScrollLayout1=(LinearLayout)findViewById(R.id.scrollLayout1);
    }

    public void addImage(int imgId)
    {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(imgId);
        mScrollLayout1.addView(imageView);

        ViewGroup.LayoutParams params=imageView.getLayoutParams();
        params.width=200;

        imageView.setOnTouchListener(this);

        arImageView.add(imageView);
        arImageRes.add(imgId);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            for(int i=0; i<arImageView.size();i++)
            {
                ImageView imageView=arImageView.get(i);

                if(v==imageView)
                {
                    int resId=arImageRes.get(i);
                    //mImageMain.setImageResource(arImageRes.get(i));
                }
            }
        }
        return true;
    }


}
