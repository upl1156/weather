package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.net.wifi.WifiConfiguration.Status.strings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_ct;
    private TextView tv_cityname;
    private ImageView iv_weather;
    private EditText et_cn;
    private Button bt_search;
    private TextView tv_high;
    private TextView tv_low;
    private TextView tv_hudity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et_cn = findViewById(R.id.et_cn);
        tv_ct = findViewById(R.id.tv_ct);
        tv_cityname = findViewById(R.id.tv_cityname);
        iv_weather = findViewById(R.id.iv_weather);
        bt_search = findViewById(R.id.bt_search);
        bt_search = findViewById(R.id.bt_search);
        tv_high = findViewById(R.id.tv_high);
        tv_low = findViewById(R.id.tv_low);
        tv_hudity = findViewById(R.id.tv_hudity);

        bt_search.setOnClickListener(this);


        String content = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=56696d9afa749552afdce7a7a2889800";
        callWeatherData(content);
    }

    static class Weather extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... address) {
            try{
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connect to URL
                connection.connect();

                //Retrieving Data
                InputStream streamIn = connection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(streamIn);

                int data = streamReader.read();
                StringBuilder weatherContent = new StringBuilder();

//sting을 쌓는 것 string data + string data or char data + char data >>new one

                while(data != -1){//data가 읽어올게 없을때 -1을 출력하기 때문
                    char ch = (char) data;
                    weatherContent.append(ch); //data를 weathercontent에 붙임
                    data = streamReader.read();//streamReader에 온전한 데이터를 들고와서 읽음

                }
                return weatherContent.toString();

            }catch (MalformedURLException e){
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();

            }

            return null;

        }

    }


    @Override
    public void onClick(View v) {
//                String cityname = et_cn.getText().toString();
//                String content = "https://openweathermap.org/data/2.5/weather?q=Seoul&appid=439d4b804bc8187953eb36d2a8c26a02;
//                callWeatherData(content);
        if (v.getId() == R.id.bt_search){
            if (et_cn.getText().toString().trim().length() > 0 ){
                String targetData = "https://openweathermap.org/data/2.5/weather?q=" + et_cn.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02";
                callWeatherData(targetData);
            }
        } else {
            Toast.makeText(this, "아무것도 적혀있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }



    private void callWeatherData(String content){
        Weather weather = new Weather();
        try{

            String dataReceived = weather.execute(content).get();
            //asynctask를 선언하고 한 작업을 실행시키는 것,content 가 string으로 가서 url으로 인식됨

            JSONObject jsonObject= new JSONObject(dataReceived);

            //City Info
            String cityInfo = jsonObject.getString("name");
            String weatherInfo = jsonObject.getString("weather");

            JSONArray arrayInfo = new JSONArray(weatherInfo);
            String iconInfo = "";

            for(int i = 0; i < arrayInfo.length(); i++){
                JSONObject dataFromArray = arrayInfo.getJSONObject(i);
                iconInfo = dataFromArray.getString("icon");

            }

            JSONObject mainInfo = jsonObject.getJSONObject("main");
            String tempData = mainInfo.getString("temp");

            setMainInfo(cityInfo,tempData);


//            textView.setText(jsonObject.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    private  void setMainInfo(String city, String temp){
        tv_cityname.setText(city.trim());
        temp+='\u2103';
        tv_ct.setText(temp.trim());
    }

    private void setIconInfo(String iconData){
        String targetIcon = "http://openweathermap.org/img/wn/" + iconData + "@2x.png";
        Uri uri = Uri.parse(targetIcon);

        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(iv_weather);

    }

    private void setTv_high(JSONObject mainObj) throws JSONException{
        String max = mainObj.getString("temp_max") + "/";
        tv_high.setText(max.toString());
    }
    private void setTv_low(JSONObject mainObj) throws JSONException{
        String low = mainObj.getString("temp_min") ;
        tv_low.setText(low.toString());
    }
    private void setTv_hudity(JSONObject mainObj) throws JSONException{
        String humidity = mainObj.getString("humidity") + "%";
        tv_hudity.setText(humidity.toString());
    }





}