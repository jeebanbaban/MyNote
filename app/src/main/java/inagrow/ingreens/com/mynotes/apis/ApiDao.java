package inagrow.ingreens.com.mynotes.apis;

import inagrow.ingreens.com.mynotes.utils.AllUrls;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 31/1/18.
 */

public class ApiDao {
    public static ApiInterface getApiDao(){

        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl(AllUrls.SERVER)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit=builder.build();
        ApiInterface apiInterface=retrofit.create(ApiInterface.class);

        return apiInterface;

    }

}
