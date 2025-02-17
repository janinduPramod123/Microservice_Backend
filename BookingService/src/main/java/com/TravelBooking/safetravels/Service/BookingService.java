package com.TravelBooking.safetravels.Service;

import HotelService.Model.HotelEntity;
import HotelService.Model.PackageEntity;
import com.TravelBooking.safetravels.Model.BookingEntity;
import com.TravelBooking.safetravels.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final WebClient webClient;
    private final WebClient notificationClient;

    @Autowired
    private BookingRepository bookingRepository;

    public BookingService(WebClient.Builder webClientBuilder, WebClient.Builder notificationClientBuilder) {
        this.notificationClient = notificationClientBuilder.baseUrl("http://localhost:8083/api/v5").build();
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v2").build();
    }

    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookingEntity getBookingById(int id) {
        Optional<BookingEntity> booking = bookingRepository.findById(id);
        return booking.orElse(null);
    }
    public List<BookingEntity> getBookingByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public String SaveBooking (BookingEntity booking)
    {

        Integer hotelID = booking.getHotel_id();
        Integer packageID = booking.getPackage_id();
        Integer days = booking.getNo_of_days();
        Integer nPackages = booking.getNo_of_packages();
        System.out.println();

        try
        {
            HotelEntity hotelResponse= webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/gethotel/{id}").build(hotelID))
                    .retrieve()
                    .bodyToMono(HotelEntity.class)
                    .block();


            if(nPackages > hotelResponse.getAvailable_packages())
            {
                return "Packages Ran Out !!";
            }
            else
            {
                PackageEntity packageResponse= webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/getpackage/{id}").build(packageID))
                        .retrieve()
                        .bodyToMono(PackageEntity.class)
                        .block();

                booking.setTotal_bill(packageResponse.getPackagePrice()*nPackages*days);



                bookingRepository.save(booking);

                int packagesUpdated = hotelResponse.getAvailable_packages() - nPackages;
                hotelResponse.setAvailable_packages(packagesUpdated);


                webClient.put()
                        .uri("/updateHotel")
                        .bodyValue(hotelResponse)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();


                notificationClient.post()
                        .uri("/booking-opened")
                        .bodyValue(booking.getBook_id())
                        .retrieve()
                        .bodyToMono(BookingEntity.class)
                        .block();


                return "Booking Successfull ...";

            }

        }
        catch(Exception e)
        {
            return e.toString();
        }

    }

    public String DeleteBooking(int id) {
        if(getBookingById(id) != null)
        {
            bookingRepository.deleteById(id);
            return "Booking deleted ...";
        }
        else
        {
            return "Booking not found !!!";
        }
    }


    public BookingEntity updateBooking(int bookingId, BookingEntity updatedBooking) {

        BookingEntity existingBooking = bookingRepository.findById(bookingId).orElse(null);

        if (existingBooking != null) {


            existingBooking.setUser_id(updatedBooking.getUser_id());
            existingBooking.setHotel_id(updatedBooking.getHotel_id());
            existingBooking.setPackage_id(updatedBooking.getPackage_id());
            existingBooking.setNo_of_days(updatedBooking.getNo_of_days());
            existingBooking.setNo_of_packages(updatedBooking.getNo_of_packages());
            existingBooking.setTotal_bill(updatedBooking.getTotal_bill());


            bookingRepository.save(existingBooking);

            return existingBooking;
        } else {
            throw new RuntimeException("Booking not found!");
        }
    }

    public BookingEntity updateBookingStatus(int bookingId, String status) {


        String newUpdate=status;
        bookingRepository.updateBookingStatus(bookingId,newUpdate);


        try
        {
            String response =notificationClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/bookingnotification/{bookingId}").build(bookingId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


                BookingEntity existingBooking = bookingRepository.findById(bookingId).orElse(null);

                System.out.println("Response : "+response);

                return existingBooking;


        }
        catch(Exception e)
        {
            System.out.println("Cannot call the Notification Service ...");

        }

        return null;

    }

}
