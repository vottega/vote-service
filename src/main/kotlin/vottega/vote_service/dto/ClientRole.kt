package vottega.vote_service.dto

enum class ClientRole {
  USER,
  PARTICIPANT;

  val roleName: String
    get() = "ROLE_$name"
}