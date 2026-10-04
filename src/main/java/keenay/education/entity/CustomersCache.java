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
@KeySpace("customers_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomersCache {

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
}
