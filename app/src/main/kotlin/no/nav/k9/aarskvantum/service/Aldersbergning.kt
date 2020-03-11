package no.nav.k9.aarskvantum.service

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

@Service
class Aldersbergning {
    fun bestemmeAlder(fødseldato: LocalDate):Boolean{
        return Period.between(fødseldato,LocalDateTime.now().toLocalDate()).years < 12
    }
}