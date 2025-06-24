package money.ultradecentralised.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "money.ultradecentralised.api")
@EntityScan({
        "money.ultradecentralised.api.entity"
})
@EnableJpaRepositories({
        "money.ultradecentralised.api.repository"
})
public class UltraDecentralisedMoneyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UltraDecentralisedMoneyApiApplication.class, args);
    }

}
