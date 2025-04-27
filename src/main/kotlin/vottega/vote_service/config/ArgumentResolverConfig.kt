package vottega.vote_service.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import vottega.vote_service.argument_resolver.ParticipantIdArgumentResolver
import vottega.vote_service.argument_resolver.RoomIdArgumentResolver

@Configuration
class ArgumentResolverConfig(
  private val roomIdArgumentResolver: RoomIdArgumentResolver,
  private val participantIdArgumentResolver: ParticipantIdArgumentResolver
) : WebMvcConfigurer {
  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    resolvers.add(roomIdArgumentResolver)
    resolvers.add(participantIdArgumentResolver)
  }
}