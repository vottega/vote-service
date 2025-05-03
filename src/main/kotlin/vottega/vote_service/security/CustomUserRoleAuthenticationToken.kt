package vottega.vote_service.security


import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import vottega.vote_service.dto.ClientRole

class CustomUserRoleAuthenticationToken(
  private val userId: Long,
) : AbstractAuthenticationToken(listOf(SimpleGrantedAuthority(ClientRole.USER.roleName))) {
  init {
    isAuthenticated = true
  }

  override fun getCredentials(): Any = ClientRole.USER.roleName
  override fun getPrincipal(): Any = userId
}