package id.orbion.ecommerce_app.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.default:100}")
    private int defaultLimitForPeriod;

    @Value("${rate.limit.period:60}")
    private int limitPerioInSeconds;

    @Value("${rate.limit.timeout:1}")
    private int timeoutInSeconds;

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(limitPerioInSeconds))
                .limitForPeriod(defaultLimitForPeriod)
                .timeoutDuration(Duration.ofSeconds(timeoutInSeconds))
                .build();
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry(RateLimiterConfig rateLimiterConfig) {
        return RateLimiterRegistry.of(rateLimiterConfig);
    }

}
