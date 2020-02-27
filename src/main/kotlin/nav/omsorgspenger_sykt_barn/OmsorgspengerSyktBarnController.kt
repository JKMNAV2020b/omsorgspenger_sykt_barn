package nav.omsorgspenger_sykt_barn

import com.google.gson.Gson
import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import no.nav.k9.søknad.SøknadValidator
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*


@RestController
class OmsorgspengerSyktBarnController {












    fun handler(personNummer:String):String{
        return personNummer
    }

    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = handler(name)

    @PostMapping(path = arrayOf("/recreq"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test2(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {

        var o:OmsorgspengerSøknad = OmsorgspengerSøknad.SerDes.deserialize(form)
        println(o.søknadId.id.toString())
        //public List<Feil> valider(OmsorgspengerSøknad søknad) {
        //var o:OmsorgspengerSøknadValidator = OmsorgspengerSøknadValidator()

        return ResponseEntity.ok().build()
    }

}