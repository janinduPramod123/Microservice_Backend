package HotelService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="hotel")
public class HotelEntity {
    @Id
    @Column(name = "hotel_id")
    int id;

    @Column(name = "address")
    private String address;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "province")
    private String province;

    @Column(name = "package_type")
    private String package_type;

    @Column(name = "price_perday")
    private int price_perday;


}
