package com.bazaar.inventorytrackingv2.securityconfig;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThrottlingInterceptor());
    }

    public class ThrottlingInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String clientIp = getClientIP(request);
            Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

            // Wait until token is available (throttling)
            long waitNanos = bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill();
            if (waitNanos > 0) {
                Thread.sleep(waitNanos / 1_000_000); // Convert to milliseconds
            }

            bucket.tryConsume(1); // Consume after wait
            return true;
        }

        private String getClientIP(HttpServletRequest request) {
            String xfHeader = request.getHeader("X-Forwarded-For");
            return (xfHeader == null) ? request.getRemoteAddr() : xfHeader.split(",")[0];
        }
    }
}
