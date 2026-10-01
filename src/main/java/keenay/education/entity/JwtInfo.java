package keenay.education.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tarantool.spring.data40.query.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
@KeySpace("jwt_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtInfo {
    @Id
    @Field("token_hash")
    @JsonProperty("tokenHash")
    private String tokenHash;

    @Field("user_id")
    @JsonProperty("userId")
    private Long userId;

    @Field("token")
    @JsonProperty("token")
    private String token;

    @Field("customer_id")
    @JsonProperty("customerId")
    private Long customerId;

    @Field("seller_id")
    @JsonProperty("sellerId")
    private Long sellerId;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("password")
    @JsonProperty("password")
    private String password;

    @Field("deleted_at")
    @JsonProperty("deletedAt")
    private Long deletedAt;

    @Field("expired_at")
    @JsonProperty("expiredAt")
    private Long expiredAt;

    @Field("created_at")
    @JsonProperty("createdAt")
    private Long createdAt;
}
