package com.abedkhan.chatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abedkhan.chatgpt.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.generateBtn.setOnClickListener(view -> {
            String text=binding.inputText.getText().toString().trim();
            if (text.isEmpty()){
                binding.inputText.setError("Need");

            }else {
                callApi(text);

            }
        });




    }

    private void callApi(String text) {

        setProgress(true);
        binding.imgcard.setVisibility(View.VISIBLE);
        binding.waittxt.setVisibility(View.VISIBLE);

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("prompt",text);
            jsonObject.put("size","256x256");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(jsonObject.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
               Toast.makeText(MainActivity.this, "Failed to generate", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
           // Log.i("tag", "response: "+ Objects.requireNonNull(response.body()).string());

//                try {
//                    JSONObject jsonObject1=new JSONObject((Objects.requireNonNull(response.body())).string());
//                    String imgUrl =jsonObject1.getJSONArray("data").getJSONObject(0).getString("url");
//                    loadImg(imgUrl);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
              //  binding.inputText.getText().clear();

                try {
                    JSONObject jsonObject1=new JSONObject(response.body().string());
           String imgUrl =jsonObject1.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImg(imgUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void loadImg(String imgUrl) {

                runOnUiThread(()->{

                    Glide.with(MainActivity.this).load(imgUrl).into(binding.image);
                    setProgress(false);
                    binding.imgcard.setVisibility(View.VISIBLE);
                    binding.waittxt.setVisibility(View.GONE);
                });
            }
        });

    }
    void setProgress(boolean isProgress){



        runOnUiThread(()->{

            if (isProgress){
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.generateBtn.setVisibility(View.GONE);
            }else {
                binding.progressbar.setVisibility(View.GONE);
                binding.generateBtn.setVisibility(View.VISIBLE);
            }
        });

    }
}