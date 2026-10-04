package keenay.education.config;

import io.tarantool.client.factory.TarantoolCrudClientBuilder;
import io.tarantool.client.factory.TarantoolFactory;
import io.tarantool.spring.data40.repository.config.EnableTarantoolRepositories;
import keenay.education.repository.tarantool.JwtInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTarantoolRepositories(basePackages = "keenay.education.repository.tarantool")
public class TarantoolConfig {

    private final String host;
    private final Integer port;
    private final String user;
    private final String password;

    public TarantoolConfig(
            @Value("${spring.data.tarantool.host}") String host,
            @Value("${spring.data.tarantool.port}") Integer port,
            @Value("${spring.data.tarantool.username}") String user,
            @Value("${spring.data.tarantool.password}") String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Bean
    public TarantoolCrudClientBuilder clientSettings() {
        return TarantoolFactory.crud()
                .withHost(host)
                .withPort(port)
                .withUser(user)
                .withPassword(password);
    }
}
