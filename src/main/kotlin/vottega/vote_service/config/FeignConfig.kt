package vottega.vote_service.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Configuration
class FeignConfig {

  @Bean
  fun requestInterceptor(): RequestInterceptor {
    return RequestInterceptor { requestTemplate: RequestTemplate ->
      val attributes =
        RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
      if (attributes != null) {
        val currentRequest = attributes.request

        val headerNames = currentRequest.headerNames
        if (headerNames != null) {
          while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            val headerValue = currentRequest.getHeader(headerName)
            requestTemplate.header(headerName, headerValue)
          }
        }
      }
    }
  }
}