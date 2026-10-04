package keenay.education.dto.users;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class SellerBodyDTO {
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private String address;
    @NonNull
    private String inn;
    @NonNull
    @Size(min = 10, max = 12)
    private String description;
}
