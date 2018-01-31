package inagrow.ingreens.com.mynotes.apis;

import inagrow.ingreens.com.mynotes.models.CreateNoteResponse;
import inagrow.ingreens.com.mynotes.models.DeleteNoteResponse;
import inagrow.ingreens.com.mynotes.models.DeleteUserResponse;
import inagrow.ingreens.com.mynotes.models.LoginResponse;
import inagrow.ingreens.com.mynotes.models.LogoutResponse;
import inagrow.ingreens.com.mynotes.models.NoteResponse;
import inagrow.ingreens.com.mynotes.models.RegisterResponse;
import inagrow.ingreens.com.mynotes.models.UpdateNoteResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by root on 31/1/18.
 */

public interface ApiInterface {
    @POST("/auth/login.json")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @POST("/auth/register.json")
    @FormUrlEncoded
    Call<RegisterResponse> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @POST("/auth/logout.json")
    @FormUrlEncoded
    Call<LogoutResponse> logout(@Field("token") String token);

    @POST("/auth/delete.json")
    @FormUrlEncoded
    Call<DeleteUserResponse> deleteUser(@Field("token") String token);

    @GET("/api/notes.json")
    Call<NoteResponse> getNotes(@Query("token") String token);

    @POST("/api/create.json")
    @FormUrlEncoded
    Call<CreateNoteResponse> createNote(@Field("token") String token, @Field("title") String title, @Field("body") String body);

    @POST("/api/update.json")
    @FormUrlEncoded
    Call<UpdateNoteResponse> updateNote(@Field("token") String token, @Field("id") int id, @Field("title") String title, @Field("body") String body);

    @POST("/api/delete.json")
    @FormUrlEncoded
    Call<DeleteNoteResponse> deleteNote(@Field("token") String token, @Field("id") int id);
}
