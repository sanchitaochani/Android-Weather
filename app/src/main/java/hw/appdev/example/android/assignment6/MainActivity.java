package hw.appdev.example.android.assignment6;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "debugging";

    TextView mText;
    ImageView mImage;
    Button mButton;
    EditText mDate;
    ImageView mIcon;
    TextView mDescription;

    String location;
    String date;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = findViewById(R.id.location_name);
        mImage = findViewById(R.id.image);
        mButton = findViewById(R.id.search_button);
        mDate = findViewById(R.id.date_field);
        mIcon = findViewById(R.id.icon);
        mDescription = findViewById(R.id.description);

        Places.initialize(getApplicationContext(),"AIzaSyAIau8IFEx9liE9aJyYyCfy4JQdtQ6ofC4" );

        final PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_search_bar);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS));


        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                location = place.getName();
                mText.setText(location);
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                String attributions = photoMetadata.getAttributions();
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    mImage.setImageBitmap(bitmap);
                    mImage.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                //something
            }
        });
    }

    public void onSearchClicked(View view) {
        //if (!isValidInput())
            //mDate.setText("");
        //else {
        date = mDate.getText().toString();
            WeatherHttpClient.getInstance().fetchWeatherInfo("json", location, date, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        //Log.i(LOG_TAG, responseString);

                        //JSONArray jsonArray = jsonObject.optJSONArray("data");
                        JSONObject weather = jsonObject.optJSONObject("data");

                        JSONArray subArray = weather.optJSONArray("current_condition");

                        //JSONObject subJSONObject = subArray.optJSONObject(0);
                        String weatherInC = subArray.optJSONObject(0).getString("temp_C").toString();
                        Log.i(LOG_TAG, weatherInC);

                        JSONObject nameJSON = weather.optJSONObject("weatherDesc");

                        String weatherDescription = nameJSON.optString("value");
                        Log.i(LOG_TAG, weatherDescription);

                        description = weatherInC + ", " + weatherDescription;
                        mDescription.setText(description);
                        JSONObject imageJSON = weather.optJSONObject("weatherIconUrl");

                        String weatherIcon = imageJSON.optString("value");

                        Log.i("TAG", weatherDescription);
                        // mTextview.setText(firstName+" "+lastName);
                        //Picasso.get().load(mediumImage).fit().centerCrop().into(mImageView);
                    } catch (Exception e) {

                    }
                    mDate.setText("");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

    }

    private boolean isValidInput() {
        if (mDate.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            date = mDate.getText().toString();
            return true;
        }
    }
}
