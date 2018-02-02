package inagrow.ingreens.com.mynotes.apis;


import inagrow.ingreens.com.mynotes.models.DeleteNoteModel;
import inagrow.ingreens.com.mynotes.models.LoginModel;
import inagrow.ingreens.com.mynotes.models.LogoutModel;
import inagrow.ingreens.com.mynotes.models.NoteListModel;
import inagrow.ingreens.com.mynotes.models.NoteModel;
import inagrow.ingreens.com.mynotes.models.RegisterModel;
import inagrow.ingreens.com.mynotes.models.UpdateNoteModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by root on 31/1/18.
 */

public interface ApiInterface {

      @POST("/auth/register.json")
      @FormUrlEncoded
      Call<RegisterModel> register(@Field("name") String name,@Field("email") String email,@Field("password") String password);

      @POST("/auth/login.json")
      @FormUrlEncoded
      Call<LoginModel> login(@Field("email") String email,@Field("password") String password);

      @POST("/api/create.json")
      @FormUrlEncoded
      Call<NoteModel> createNote(@Field("token") String token,@Field("title") String title,@Field("body") String body);

      @GET("/api/notes.json")
      Call<NoteListModel> getNotes(@Query("token") String token);

      @POST("/api/delete.json")
      @FormUrlEncoded
      Call<DeleteNoteModel> deleteNote(@Field("token") String token,@Field("id") int id);

      @POST("/api/update.json")
      @FormUrlEncoded
      Call<UpdateNoteModel> updateNote(@Field("token") String token,@Field("id") String id,@Field("title") String title,@Field("body") String body);

      @POST("/auth/logout.json")
      @FormUrlEncoded
      Call<LogoutModel> logout(@Field("token") String token);



}
