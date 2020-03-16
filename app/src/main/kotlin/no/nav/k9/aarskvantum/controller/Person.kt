package no.nav.k9.aarskvantum.controller

import java.time.LocalDate
import java.time.ZonedDateTime

data class Person(
        val norskIdentitetsnummer:String?,
        val barn:List<Person>?= emptyList(),
        val foreldre:List<Person>?= emptyList(),
        var partner:List<Person>? = emptyList(),
        var tidligerepartnere:List<Person>? = emptyList(),
        val bosted:String?,
        val medlemskap:String?,
        val sosialStatus:String?,
        val arbeidsStatus:String?,
        val alder:String?,
        val kronisktSykt:Boolean?,
        var omsorgspengerperaar:Int?
) {

    // to do put values in properties
    fun rate():Long{
        val aldersrate = kronisktSykt?.let {
            if(it){18L}
            else{
                12L
            }
        }?:{
            12L
        }.invoke()
        return aldersrate
    }
    fun alder():Boolean{
        val fødseldato = norskIdentitetsnummer?.let{
            var fødselsnummer = it.subSequence(0, 6)
            var dager = Integer.parseInt(fødselsnummer.substring(0, 2)).toString()
            if (dager.length == 1) {
                dager = "0" + dager
            }
            var måneder = Integer.parseInt(fødselsnummer.substring(2, 4)).toString()
            if (måneder.length == 1) {
                måneder = "0" + måneder
            }
            var år = Integer.parseInt(fødselsnummer.substring(4, 6))
            var alderfraPersonNummer = LocalDate.now().year.toString().substring(0, 2) + år + "-" + måneder + "-" + dager + "T00:00:00.000Z"
            alderfraPersonNummer
        }?:{
            val fødseldato = alder?.let{
                it+ "T00:00:00.000Z"
            }?:{
                // add some sort of logging here...
                // and figure out what to do with persons that does
                ""
            }.invoke()
            fødseldato
        }.invoke();

        if(ZonedDateTime.now().minusYears(rate()).compareTo(ZonedDateTime.parse(fødseldato)) < 0){
            return true
        }
        return false
    }

    fun omsorgspenger():Boolean{
        var omsorgpenger = 0L
        var antallBarnMedOmsorgspenger = 0
        var maxBarnAlder = 0L
        var kronisktSykt = false

        if((this.arbeidsStatus in arrayOf("arbeidstaker","frilans","selvstendig"))){
            omsorgpenger=10L
        }

        this.barn?.forEach(){ it ->
            if(it.alder()){
                antallBarnMedOmsorgspenger+=1
                if(maxBarnAlder<it.rate()){
                    maxBarnAlder = it.rate()
                }
                if(it.kronisktSykt.let { it } == true){
                    kronisktSykt = true
                }
            }
        }

        if(antallBarnMedOmsorgspenger >= 3){
            omsorgpenger+=5;
        }
        if(kronisktSykt){
            omsorgpenger*=2L;
        }
        if(!(this.sosialStatus in arrayOf("gift","samboer"))){
            omsorgpenger*=2L
        }
        println(omsorgpenger.toInt())
        this.omsorgspengerperaar = omsorgpenger.toInt()
        return true
    }
}