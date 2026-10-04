package keenay.education.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tarantool.spring.data40.query.Field;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
@KeySpace("sellers_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerCache {
    @Id
    @Field("id")
    @JsonProperty("id")
    private Long id;

    @Field("bucket_id")
    @JsonProperty("bucketId")
    private Long bucketId;

    @Field("name")
    @JsonProperty("name")
    private String name;

    @Field("surname")
    @JsonProperty("surname")
    private String surname;

    @Field("address")
    @JsonProperty("address")
    private String address;

    @Field("inn")
    @JsonProperty("inn")
    private String inn;

    @Field("description")
    @JsonProperty("description")
    private String description;

    @Field("rating")
    @JsonProperty("rating")
    private Double rating;
}
