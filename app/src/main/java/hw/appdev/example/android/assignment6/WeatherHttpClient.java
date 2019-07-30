package hw.appdev.example.android.assignment6;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherHttpClient {
    final String API_KEY = "a46ea223ee11461fbdd10110190305";


    private static String BASE_URL = "https://api.worldweatheronline.com/premium/v1/";
    private static WeatherHttpClient mInstance = new WeatherHttpClient();

    public static WeatherHttpClient getInstance() {
        return mInstance;
    }

    Retrofit mRetrofit;
    private WeatherService mService;


    private WeatherHttpClient() {
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
        mService = mRetrofit.create(WeatherService.class);
    }

    private interface WeatherService {
        @GET("weather.ashx?key=a46ea223ee11461fbdd10110190305")
        Call<ResponseBody> getWeather(@Query("format") String format,
                                      @Query("q") String location,
                                      @Query("date_format") String Date);
    }

    public void fetchWeatherInfo(String format, String location, String date, Callback<ResponseBody> callback) {
        mService.getWeather(format, location, date).enqueue(callback);
    }
}
