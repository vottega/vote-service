package vottega.vote_service.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import vottega.vote_service.dto.ClientRole
import java.util.*

class CustomHeaderAuthenticationFilter : OncePerRequestFilter() {

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val headerRole = request.getHeader("X-Client-Role")
    println(request.getHeader("X-User-Id"))
    val userId = request.getHeader("X-User-Id")?.toLongOrNull()
    val participantUUID = try {
      UUID.fromString(request.getHeader("X-Participant-Id"))
    } catch (e: Exception) {
      null
    }
    val roomId = request.getHeader("X-Room-Id")?.toLongOrNull()
    val role = try {
      ClientRole.valueOf(headerRole)
    } catch (e: Exception) {
      null
    }
    if (
      role == null ||
      (role == ClientRole.PARTICIPANT && (participantUUID == null || roomId == null)) ||
      (role == ClientRole.USER && userId == null)
    ) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
      return
    }

    val authentication: Authentication = when {
      role == ClientRole.USER -> CustomUserRoleAuthenticationToken(userId!!)
      role == ClientRole.PARTICIPANT -> CustomParticipantRoleAuthenticationToken(
        participantUUID!!,
        roomId!!
      )

      else -> {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
        return
      }
    }
    SecurityContextHolder.getContext().authentication = authentication
    filterChain.doFilter(request, response)

  }
}