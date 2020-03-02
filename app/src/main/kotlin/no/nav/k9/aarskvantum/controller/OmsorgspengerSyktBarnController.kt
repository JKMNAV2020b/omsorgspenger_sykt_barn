package no.nav.k9.aarskvantum.controller

import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class OmsorgspengerSyktBarnController {

    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "hello"

    @PostMapping(path = arrayOf("/recreq"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test2(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {

        val o: OmsorgspengerSøknad = OmsorgspengerSøknad.SerDes.deserialize(form)
        println(o.søknadId.id.toString())
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