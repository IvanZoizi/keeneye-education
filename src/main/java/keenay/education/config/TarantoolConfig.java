package keenay.education.config;

import io.tarantool.client.factory.TarantoolCrudClientBuilder;
import io.tarantool.client.factory.TarantoolFactory;
import io.tarantool.spring.data40.repository.config.EnableTarantoolRepositories;
import keenay.education.repository.tarantool.JwtInfoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTarantoolRepositories(basePackages = "keenay.education.repository.tarantool")
public class TarantoolConfig {
    @Bean
    public TarantoolCrudClientBuilder clientSettings() {
        return TarantoolFactory.crud()
                .withHost("localhost")
                .withPort(3301)
                .withUser("app")
                .withPassword("admin");
    }
}
