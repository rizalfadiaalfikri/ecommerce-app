package id.orbion.ecommerce_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xendit.Xendit;

@Configuration
public class XenditConfig {

    @Value("${xendit.api-key}")
    private String xenditApiKey;

    @Bean
    public Xendit xenditClient() {
        Xendit.apiKey = xenditApiKey;
        return new Xendit();
    }

}
