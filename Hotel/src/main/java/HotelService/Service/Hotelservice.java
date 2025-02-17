package HotelService.Service;

import HotelService.Model.HotelEntity;
import HotelService.Repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Hotelservice {
    @Autowired
    private HotelRepository hotelRepository;

    public List<HotelEntity> getAllHotels() {
        return hotelRepository.findAll();
    }

    public HotelEntity getHotelById(int id) {
        Optional<HotelEntity> hotel = hotelRepository.findById(id);
        return hotel.orElse(null);
    }

    public void SaveHotel(HotelEntity hotel)
    {
        hotelRepository.save(hotel);
    }

    public String DeleteHotel(int id) {
        if(getHotelById(id) != null)
        {
            hotelRepository.deleteById(id);
            return "Hotel deleted ...";
        }
        else
        {
            return "Hotel not found !!!";
        }
    }

    public String updateHotel(HotelEntity hotel) {
        Optional<HotelEntity> existingHotel = hotelRepository.findById(hotel.getId());
        if (existingHotel.isPresent()) {

            HotelEntity updatedHotel = existingHotel.get();
            updatedHotel.setAddress(hotel.getAddress());
            updatedHotel.setTelephone(hotel.getTelephone());
            updatedHotel.setProvince(hotel.getProvince());
            updatedHotel.setAvailable_packages(hotel.getAvailable_packages());
            hotelRepository.save(updatedHotel);
            return "Hotel updated successfully!";
        } else {
            return "Hotel not found!";
        }
    }

}
