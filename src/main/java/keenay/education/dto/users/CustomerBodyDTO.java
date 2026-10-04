package keenay.education.dto.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class CustomerBodyDTO {
    @NonNull
    private String name;
    @NonNull
    private String surname;
}
