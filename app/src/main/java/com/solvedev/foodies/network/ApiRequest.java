package com.solvedev.foodies.network;

import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.FoodResponse;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.model.Users;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRequest {

    @FormUrlEncoded
    @POST("login_user.php")
    Call<ResponsModel> loginUser(@Field("email") String email,
                                 @Field("password") String password);

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponsModel> registerAccount(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password,
                                       @Field("notelp") String notelp);

    @FormUrlEncoded
    @POST("update_profile.php")
    Call<ResponsModel> updateProfile(@Field("iduser") String iduser,
                                     @Field("nama") String nama,
                                     @Field("email") String email,
                                     @Field("notelp") String notelp,
                                     @Field("nama_resto") String nama_resto,
                                     @Field("alamat") String alamat,
                                     @Field("lat") String lat,
                                     @Field("lng") String lng,
                                     @Field("gambar") String gambar);

    @FormUrlEncoded
    @POST("update_status_cart.php")
    Call<ResponsModel> updateStatusCart(@Field("iduser") String iduser);


    @FormUrlEncoded
    @POST("tambah_produk.php")
    Call<ResponsModel> tambahProduk(@Field("iduser") String iduser,
                                    @Field("nama") String nama,
                                    @Field("harga") String harga,
                                    @Field("kategori") String kategori,
                                    @Field("gambar") String gambar);

    @FormUrlEncoded
    @POST("tambah_pesanan.php")
    Call<ResponsModel> tambahPesanan(@Field("id_pembeli") String idpembeli,
                                     @Field("total_harga") int total,
                                     @Field("alamat") String alamat,
                                     @Field("pengiriman") String pengiriman,
                                     @Field("lat") double lat,
                                     @Field("lng") double lng);


    @FormUrlEncoded
    @POST("tambah_keranjang.php")
    Call<ResponsModel> tambahKeranjang(@Field("id_food") String idfood,
                                       @Field("id_user") String iduser);

    @FormUrlEncoded
    @POST("update_produk.php")
    Call<ResponsModel> updateProduk(@Field("iduser") String iduser,
                                    @Field("nama") String nama,
                                    @Field("id_food") String id_food,
                                    @Field("harga") String harga,
                                    @Field("kategori") String kategori,
                                    @Field("gambar") String gambar);

    @FormUrlEncoded
    @POST("delete_produk.php")
    Call<ResponsModel> deleteProduk(@Field("id_food") String id_food);

    @FormUrlEncoded
    @POST("delete_cart.php")
    Call<ResponsModel> deleteCart(@Field("id_cart") String id_cart);



    @GET("list_makanan_kategori.php")
    Call<Food> getFoodCategory(@Query("kategori") String kategori);

    @GET("list_transaksi_pembeli.php")
    Call<Food> getTransaksiBuyer(@Query("idpembeli") String id);

    @GET("list_transaksi_penjual.php")
    Call<Food> getTransaksiSeller(@Query("idpenjual") String id);

    @GET("list_makanan_keranjang.php")
    Call<Food> getFoodCart(@Query("id_pembeli") String idpembeli);

    @GET("list_makanan_penjualan.php")
    Call<Food> getFoodSeller(@Query("id_user") String id);

    @GET("get_detail_profile.php")
    Call<Users> getDetailProfile(@Query("id_user") String id);

    @GET("get_detail_food.php")
    Call<Food> getDetailFood(@Query("id_food") String id);

    @GET("get_detail_resto_cart.php")
    Call<Users> getDetailRestoCart(@Query("id_user") String id);

    @GET("list_makanan_status.php")
    Call<Food> getFoodStatus(@Query("status") String status);
}
