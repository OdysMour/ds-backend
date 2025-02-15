package gr.odys.ds_backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, BucketInfo> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.capacity:100}")
    private int capacity;

    @Value("${rate.limit.refill-tokens:10}")
    private int refillTokens;

    @Value("${rate.limit.refill-duration:1}")
    private int refillDuration;

    private static class BucketInfo {
        final Bucket bucket;
        Instant lastAccessTime;

        BucketInfo(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = Instant.now();
        }

        void updateAccessTime() {
            this.lastAccessTime = Instant.now();
        }
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(capacity)
            .refillGreedy(refillTokens, Duration.ofSeconds(refillDuration))
            .build();
            
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientIdentifier(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // Skip rate limiting for health check endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = getClientIdentifier(request);
        BucketInfo bucketInfo = buckets.computeIfAbsent(clientId,
            k -> new BucketInfo(createNewBucket()));
        bucketInfo.updateAccessTime();

        if (bucketInfo.bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setContentType("application/json");
            // Estimate wait time based on available tokens
            long availableTokens = bucketInfo.bucket.getAvailableTokens();
            long estimatedWaitMs = availableTokens == 0 ?
                (refillDuration * 1000) / refillTokens : 0;
            response.getWriter().write(String.format(
                "{\"error\": \"Rate limit exceeded\", \"retryAfterMs\": %d}",
                estimatedWaitMs
            ));
        }
    }

    @Scheduled(fixedRate = 300000) // Clean up every 5 minutes
    public void cleanupOldBuckets() {
        Instant now = Instant.now();
        Duration expiry = Duration.ofHours(1);
        
        buckets.entrySet().removeIf(entry ->
            Duration.between(entry.getValue().lastAccessTime, now).compareTo(expiry) > 0);
    }
}