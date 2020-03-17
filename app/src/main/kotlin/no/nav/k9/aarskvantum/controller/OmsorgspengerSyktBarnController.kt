package no.nav.k9.aarskvantum.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.k9.aarskvantum.service.OmsorgspengerBeregning
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class OmsorgspengerSyktBarnController(val omsorgspengerBeregningService: OmsorgspengerBeregning) {

    @PostMapping(path = arrayOf("/soknad"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun søknad(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {
        val mapper = jacksonObjectMapper()
        if(omsorgspengerBeregningService.beregn(form)){
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.status(500).build()
    }

    @GetMapping
    @RequestMapping("/visomsorgspenger")
    fun visomsorgspenger(@RequestParam(value = "norskIdentitetsnummer", defaultValue = "") verdi: String):ResponseEntity<String>{
        return ResponseEntity("{\"omsorgspenger\" : "+omsorgspengerBeregningService.vis(verdi)+"}", null, HttpStatus.OK)
    }

}