package money.ultradecentralised.api.config;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.conversions.CardanoConverters;
import org.cardanofoundation.conversions.ClasspathConversionsFactory;
import org.cardanofoundation.conversions.domain.NetworkType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppConf {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Network network(@Value("${network}") String network) {
        var actualNetwork = switch (network) {
            case "preprod" -> Networks.preprod();
            case "preview" -> Networks.preview();
            default -> Networks.mainnet();
        };
        log.info("INIT network: {}, network type: {}", network, actualNetwork);
        return actualNetwork;
    }


    @Bean
    public CardanoConverters cardanoConverters(@Value("${network}") String network) {
        var networkType = switch (network) {
            case "preprod" -> NetworkType.PREPROD;
            case "preview" -> NetworkType.PREVIEW;
            default -> NetworkType.MAINNET;
        };
        log.info("INIT network: {}, network type: {}", network, networkType);
        return ClasspathConversionsFactory.createConverters(networkType);
    }
}
