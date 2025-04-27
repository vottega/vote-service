package vottega.vote_service.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(order = 0) // PreAuthorize 가 Transaction 내에서 동작하도록 순서를 0으로 지정
class TransactionConfig