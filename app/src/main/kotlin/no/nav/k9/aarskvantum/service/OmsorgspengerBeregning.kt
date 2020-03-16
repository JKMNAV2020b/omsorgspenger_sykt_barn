package no.nav.k9.aarskvantum.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.k9.aarskvantum.controller.Person
import org.springframework.stereotype.Service

@Service
class OmsorgspengerBeregning {

    lateinit var database:HashMap<String?, Person>
    init{
        database = hashMapOf<String?, Person>()
        // init from json file...eventually..
    }

    fun beregn(søknad:String):Boolean{
        val mapper = jacksonObjectMapper()

        try {
            var søker:Person = mapper.readValue<Person>(søknad)
            if(søker.omsorgspenger()){
                database[søker.norskIdentitetsnummer] = søker
            }
        }
        catch (exception:Exception){
            return false
        }
        return true
    }

    fun vis(norskIdentitetsnummer: String):Int{
        return database[norskIdentitetsnummer]?.let { it->it.omsorgspengerperaar }?:{0}.invoke()
    }

}