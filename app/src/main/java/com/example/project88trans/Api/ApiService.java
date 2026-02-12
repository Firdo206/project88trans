package com.example.project88trans.Api;

import com.example.project88trans.model.PaketWisata;
import com.example.project88trans.model.Promo;
import com.example.project88trans.model.user;
import com.example.project88trans.model.UserResponse;
import com.example.project88trans.model.DefaultResponse;
import com.example.project88trans.model.bus;
import com.example.project88trans.model.BusRental;
import com.example.project88trans.model.TourRental;
import com.example.project88trans.model.Payment;
import com.example.project88trans.model.TourRentalCreate;
import com.example.project88trans.model.BusRentalCreate;
import com.example.project88trans.model.InvoiceData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    // ============== AUTH ==============
    @FormUrlEncoded
    @POST("auth/register.php")
    Call<ApiResponse<user>> register(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/login.php")
    Call<ApiResponse<user>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/google_login.php")
    Call<ApiResponse<user>> googleLogin(
            @Field("name") String name,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("auth/get_user.php")
    Call<ApiResponse<UserResponse.User>> getUserByEmail(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("auth/update_user.php")
    Call<DefaultResponse> updateUser(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("auth/change_password.php")
    Call<ApiResponse<Void>> changePassword(
            @Field("email") String email,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword
    );

    // ============== BUS ==============
    @GET("armada/Bus.php")
    Call<ApiResponse<List<bus>>> getBuses();

    // Tambahkan endpoint untuk get bus by id jika ada
    @GET("armada/Bus.php")
    Call<ApiResponse<bus>> getBusById(@Query("id") int busId);

    @FormUrlEncoded
    @POST("bus_rentals/create.php")
    Call<ApiResponse<BusRentalCreate>> addBusRental(
            @Field("user_id") int userId,
            @Field("bus_id") int busId,
            @Field("start_date") String startDate,
            @Field("end_date") String endDate,
            @Field("promo_code") String promoCode
    );

    @FormUrlEncoded
    @POST("tour_rentals/create.php")
    Call<ApiResponse<TourRentalCreate>> addTourRental(
            @Field("user_id") int userId,
            @Field("package_id") int packageId,
            @Field("number_of_people") int numberOfPeople,
            @Field("start_date") String startDate,
            @Field("total_price") double totalPrice,
            @Field("promo_code") String promoCode
    );

    @GET("paket/PaketWisata.php")
    Call<ApiResponse<List<PaketWisata>>> getAllPaketWisata();

    // ============== PROMO ==============
    @GET("promo/promo.php")
    Call<ApiResponse<List<Promo>>> getAllPromo();

    @FormUrlEncoded
    @POST("promo/apply_promo.php")
    Call<ApiResponse<Promo>> getPromoByCode(
            @Field("promo_code") String promoCode
    );

    @FormUrlEncoded
    @POST("bus_rentals/get_by_user.php")
    Call<ApiResponse<List<BusRental>>> getBusRentalsByUser(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("tour_rentals/get_by_user.php")
    Call<ApiResponse<List<TourRental>>> getTourRentalsByUser(@Field("user_id") int userId);

    // ============== PAYMENT ==============
    @Multipart
    @POST("payment/update_payment.php")
    Call<ApiResponse<Payment>> updatePaymentWithBukti(
            @Part("user_id") RequestBody userId,
            @Part("rental_type") RequestBody rentalType,
            @Part("rental_id") RequestBody rentalId,
            @Part("promo_code") RequestBody promoCode,
            @Part("payment_method") RequestBody paymentMethod,
            @Part("total_amount") RequestBody totalAmount,
            @Part MultipartBody.Part bukti
    );

    // Method helper untuk CASH
    default Call<ApiResponse<Payment>> updatePaymentCash(
            int userId,
            String rentalType,
            int rentalId,
            String promoCode,
            double totalAmount,
            String paymentMethod
    ) {
        RequestBody userIdBody = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(userId));
        RequestBody rentalTypeBody = RequestBody.create(okhttp3.MultipartBody.FORM, rentalType);
        RequestBody rentalIdBody = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(rentalId));
        RequestBody promoCodeBody = RequestBody.create(okhttp3.MultipartBody.FORM, promoCode != null ? promoCode : "");
        RequestBody paymentMethodBody = RequestBody.create(okhttp3.MultipartBody.FORM, paymentMethod);
        RequestBody totalAmountBody = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(totalAmount));

        return updatePaymentWithBukti(userIdBody, rentalTypeBody, rentalIdBody,
                promoCodeBody, paymentMethodBody, totalAmountBody, null);
    }

    // ============== INVOICE ==============
    @GET("detail/get_invoice_detail.php")
    Call<ApiResponse<InvoiceData>> getInvoice(
            @Query("type") String type,
            @Query("id") String id
    );
}