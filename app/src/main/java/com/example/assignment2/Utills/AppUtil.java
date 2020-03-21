package com.example.assignment2.Utills;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment2.DBController;
import com.example.assignment2.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppUtil {
    public static final String TAG = AppUtil.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return isNetwork;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setImage(Context context, String url, ImageView imageView) {
        Log.d("AppUtil ", url);
        if (url != null) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .timeout(5000)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image);
//                .override(500, 500);

            Glide.with(context).asBitmap().load(url)
                    .apply(requestOptions).into(imageView);
        }
    }

    public static String convertLongToDate(Long time) {
        DateFormat simple = new SimpleDateFormat("dd, MMM yyyy");
        Date result = new Date(time);
        System.out.println(simple.format(result));
        return simple.format(result);
    }

    /**
     * for decoding data
     *
     * @param inputData
     * @return
     */
    public static String getHtml(String inputData) {
        Spanned data = Html.fromHtml("");
        if (inputData != null && !inputData.equals("null"))
            data = Html.fromHtml(inputData.replaceAll("&#xa;", "<br>"));
        String result = data.toString();
        if (result.contains("<br>")) result = getHtml(result);
        return result;
    }

    public static void createDatabase(Context context) {
        BufferedReader reader = null;
        Log.d(TAG, "createDatabase called");
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("1000_place_temples.csv")));
            String mLine;
            reader.readLine();
            while ((mLine = reader.readLine()) != null) {
                Log.d(TAG, "\n" + mLine);
                DBController.getInstance(context).insertPlaceDetails(mLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
