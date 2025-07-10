package vottega.vote_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import vottega.vote_service.security.CustomHeaderAuthenticationFilter

@Configuration
class SecurityConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .logout { it.disable() }
      .anonymous { it.disable() }
      .logout { it.disable() }
      .formLogin { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 관리 비활성화
      .authorizeHttpRequests {
        it.requestMatchers(
          "/api/room/**",
        ).authenticated()
          .anyRequest().permitAll()
      }
      .addFilterAt(CustomHeaderAuthenticationFilter(), BasicAuthenticationFilter::class.java)

    return http.build()
  }
}