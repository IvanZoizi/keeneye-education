package keenay.education.dto.users;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerDTO {
    private Long id;
    private String name;
    private String surname;
    private String address;
    private String inn;
    private String description;
    private Double rating;
}
