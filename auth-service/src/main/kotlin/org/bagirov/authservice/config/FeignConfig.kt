package org.bagirov.authservice.config

import feign.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class FeignConfig {

    @Bean
    fun feignOptions(): Request.Options {
        return Request.Options(
            Duration.ofSeconds(2),  // Таймаут соединения (connect timeout)
            Duration.ofSeconds(5),  // Таймаут ожидания ответа (read timeout)
            true  // Разрешать ли переадресацию (follow redirects)
        )
    }
}
