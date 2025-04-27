package vottega.vote_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import vottega.vote_service.security.CustomHeaderAuthenticationFilter

@Configuration
class SecurityConfig {

  @Bean
  @Profile("local")
  @Order(1)
  fun webSecurityCustomizer(http: HttpSecurity): SecurityFilterChain {

    http
      .securityMatcher(
        AntPathRequestMatcher("/**")
      )
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .authorizeHttpRequests {
        it.anyRequest().permitAll()
      }
    return http.build()
  }

  @Bean
  @Order(2)
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .logout { it.disable() }
      .anonymous { it.disable() }
      .logout { it.disable() }
      .formLogin { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 관리 비활성화
      .authorizeHttpRequests {
        it.requestMatchers("/error").permitAll() // /error 경로는 인증 없이 허용
          .anyRequest().authenticated() // 그 외의 요청은 인증 필요
      }

    http.addFilterAfter(CustomHeaderAuthenticationFilter(), ExceptionTranslationFilter::class.java)

    return http.build()
  }
}