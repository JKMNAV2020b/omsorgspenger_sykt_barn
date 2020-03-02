package no.nav.k9.aarskvantum.controller

import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter





@RestController
class OmsorgspengerSyktBarnController {

    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "hello"

    @PostMapping(path = arrayOf("/recreq"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test2(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {

        try {
            val o: OmsorgspengerSøknad = OmsorgspengerSøknad.SerDes.deserialize(form)
            println(o.søknadId.id.toString())


            /*
            * 1. sjekke om brukeren finnes i databasen, eller sjekke om databasen er der i det heletatt...
            *
            * */

            println(o.barn.fødselsdato)
            val current = LocalDateTime.now()
            println(o.barn.fødselsdato.atStartOfDay())

            // fant ut om brukeren som ble sendt inn var under eller lik 12.

            var date = LocalDate.parse(o.barn.fødselsdato.toString())
            println(Period.between(o.barn.fødselsdato,current.toLocalDate()).years)










        }catch (e:Exception){
            println(e.toString())
        }



        return ResponseEntity.ok().build()
    }

    @PostMapping(path = arrayOf("/utvidetrett"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun utvidrett(@RequestBody body: String):ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @PostMapping(path = arrayOf("/midlertidigalene"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun midlertidalene(@RequestBody body: String):ResponseEntity<Unit> {

        println(body)
        return ResponseEntity.ok().build()
    }

    // to usecaes for å overføre dager
    // 1. overføre samværesdager til barnets far/mor
    // 2. overføre samværesdager til ny kone/samboer/kjæreste

    @PostMapping(path = arrayOf("/overførdager"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun overførDager(@RequestBody body: String):ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @PostMapping(path = arrayOf("/fordeldager"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun fordelDager(@RequestBody body: String):ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @PostMapping(path = arrayOf("/utbetal"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun utbetal(@RequestBody body: String):ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

}