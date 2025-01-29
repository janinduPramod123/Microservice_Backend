package com.Notification.NotificationService.Service;

import com.TravelBooking.safetravels.Model.BookingEntity;
import com.User.UserService.Model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class NotificationService {
    private final WebClient webClient;
    private final WebClient UserClient;


    @Autowired
    public NotificationService(WebClient.Builder webClientBuilder, WebClient.Builder userClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/v4").build();
        this.UserClient = userClientBuilder.baseUrl("http://localhost:8082/api/v3").build();

    }

    public void processBookingNotification(int bookingId) {
        BookingEntity booking = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getbooking/{id}").build(bookingId))
                .retrieve()
                .bodyToMono(BookingEntity.class)
                .block();

        System.out.println("Booking Status : "+booking.getBooking_status());

    }

    public void processUserNotification(int userId) {
        try
        {
            UserEntity user = UserClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/getuser/{id}").build(userId))
                    .retrieve()
                    .bodyToMono(UserEntity.class)
                    .block();

            System.out.println("User Name : "+user.getUsername());
        }
        catch (Exception e)
        {
            System.out.println("Something went Wrong !!!");
        }

    }

    public List<BookingEntity> processBookingByUserId(int userId) {
        try
        {
            List<BookingEntity> booking = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/getbookingbyuserid/{userId}").build(userId))
                    .retrieve()
                    .bodyToFlux(BookingEntity.class)
                    .collectList()
                    .block();

            return booking;


        }
        catch (Exception e)
        {
            System.out.println("Something went Wrong !!!");
        }

        return null;

    }

    public String processBookingStatus(int bookingId) {
        try
        {
            BookingEntity booking = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/getbooking/{id}").build(bookingId))
                    .retrieve()
                    .bodyToMono(BookingEntity.class)
                    .block();

            return booking.getBooking_status();


        }
        catch (Exception e)
        {
            return "Something went Wrong !!!";
        }

    }



}
