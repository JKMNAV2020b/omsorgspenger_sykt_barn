package no.nav.k9.aarskvantum.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.k9.aarskvantum.service.Aldersbergning
import no.nav.k9.aarskvantum.service.OmsorgspengerBeregning
import no.nav.k9.søknad.felles.NorskIdentitetsnummer
import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime


data class PersonEntry(
        val person:Person,
        val omsorgsdager:Int?
)

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
        this.omsorgspengerperaar = omsorgpenger.toInt()
        return true
    }
}

data class TestModel(
        val id: Int,
        val navn:String
)



@RestController
class OmsorgspengerSyktBarnController(val aldersbergning: Aldersbergning, val omsorgspengerBeregningService: OmsorgspengerBeregning) {


    lateinit var database:HashMap<String?,PersonEntry?>

    init{
        database = hashMapOf<String?, PersonEntry?>()
    }

    fun listpartners(v:String):String{
        val bb = database[v]?.person?.partner?.size
        return v+"---"+bb
    }
    fun webpage(id:String):String{

        println(id)

        var html="<html><body><form method=\"GET\" action=\"http://localhost:8090/web-sak?id=kko\"><input type=\"text\" name=\"tes\"><input type=\"submit\"></input></form>"
        var fig=""
        var pig=""
        var pstatus = ""
        var bstatus = ""
        var mstatus = ""
        var sstatus = ""
        var fpenger = ""

        if(id.length > 0) {
            if(database.contains(id)){
                println(database.contains(id))
                var personentry:PersonEntry = database[id] as PersonEntry
                var person = personentry.person

                println("*******")
                println(person.partner?.size)

                for (i in 1..person.partner?.size as Int) {
                    var nid = person!!.partner?.get(i-1)
                    println(nid?.norskIdentitetsnummer)
                    pig+="""
                        <p/><div><div style="display: inline;">&nbsp;&nbsp;partner # """+i+""":</div><div style="display: inline;"><input id="partv""""+i+""" value="""+nid?.norskIdentitetsnummer+"""></div></div>
                    """.trimIndent()
                }

                for (i in 1..person.barn?.size as Int) {
                    var cid = person!!.barn?.get(i-1)

                    println(cid?.norskIdentitetsnummer)
                    fig+="""
                        <p/><div><div style="display: inline;">&nbsp;&nbsp;barn # """+i+""":</div><div style="display: inline;"><input id="barnv""""+i+""" value="""+cid?.norskIdentitetsnummer+"""></div></div>
                    """.trimIndent()
                }

                var muligheter = listOf("arbeidstaker","frilans","selvstendig","arbeidsløs")

                var mymenu= """<select id='menu' 
                    onchange='e = document.getElementById("menu");if(e.options[e.selectedIndex].value == "arbeidsløs"){document.getElementById("omsorgsdager").value=0;}else{document.getElementById("omsorgsdager").value=10}'>""".trimMargin()
                for(i in muligheter){
                    var sx = ""
                    if(i.equals(person.arbeidsStatus)){
                        sx = " selected"
                    }
                    mymenu+="<option value='"+i+"' "+sx+">"+i+"</option>"
                        //mymenu+="""<option value="""+i+"+sx+""">"""+i+"""</option>"""
                }
                mymenu+="</select>"
                pstatus+="""
                    <p/><div><div style="display: inline;">&nbsp;&nbsp;arbeidstatus : </div><div style="display: inline;">
                    """+mymenu+"""
                    </div></div>
                """.trimIndent()

                bstatus+="""
                    <p/><div><div style="display: inline;">&nbsp;&nbsp;bosted : </div><div style="display: inline;"><input type="text" value="""+person.bosted+"""></input></div></div>
                """.trimIndent()



                mstatus+="""
                    <p/><div><div style="display: inline;">&nbsp;&nbsp;medlem : </div><div style="display: inline;"><input type="text" value="""+person.medlemskap+"""></input></div></div>
                """.trimIndent()

                muligheter = listOf("gift","enslig","samboer")
                mymenu = """<select id=menu>"""
                for(i in muligheter){
                    var sx = ""
                    if(i.equals(person.sosialStatus)){
                        sx = " selected"
                    }
                    mymenu+="<option value='"+i+"' "+sx+">"+i+"</option>"
                }
                mymenu+="</select>"

                sstatus+="""
                    <p/><div><div style="display: inline;">&nbsp;&nbsp;sosialstatus : </div><div style="display: inline;">"""+mymenu+"""</div></div>
                """.trimIndent()

                fpenger+="""
                    <p/><div><div style="display: inline;">&nbsp;&nbsp;omsorgsdager : </div><div style="display: inline;"><input type="text" id="omsorgsdager" value="""+personentry.omsorgsdager+"""></input></div></div>
                """.trimIndent()

                html+="""<div style="background-color:#cccccc;width:30%">"""
                html+="<div>&nbsp;</div>"
                html+=pstatus
                html+=bstatus
                html+=sstatus
                html+=mstatus
                html+=fig
                html+=pig
                html+=fpenger
                html+="<div>&nbsp;</div>"
                html+="</div>"
            }

        }

        html+="</body></html>"
        return html
    }


    @GetMapping
    @RequestMapping("/web-sak")
    fun test3(@RequestParam(value = "tes", defaultValue = "") name: String) = webpage(name)
    //fun test3(@RequestParam(value = "tes", defaultValue = "") name: String) = " <html><body><form method=\"GET\" action=\"http://localhost:8090/web-sak?id=kko\"><input type=\"text\" name=\"tes\"><input type=\"submit\"></input><h3></h3></body></html></form>"


    fun fåomsorgspenger(id:String) : ResponseEntity<ByteArray>{
        val output:ResponseEntity<ByteArray> = database[id]?.let {
            it ->
            it.omsorgsdager
        }.let {
            ResponseEntity("{}".toByteArray(), null, HttpStatus.OK)
        }?:{
            ResponseEntity("{}".toByteArray(), null, HttpStatus.OK)
        }.invoke()
        return output
    }

    @GetMapping
    @RequestMapping("/omsorgspenger")
    fun omsorgspenger(@RequestParam(value = "personNummer", defaultValue = "") name: String) = fåomsorgspenger(name)

    @GetMapping
    @RequestMapping("/listpartner")
    fun listpartner(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "---<"+listpartners(name)
    
    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "hello"

    fun regnutSamvær(person:Person):Int{

        // sjekk om

        var omsorgpenger = 0L
        var antallBarnMedOmsorgspenger = 0
        var maxBarnAlder = 0L
        var kronisktSykt = false

        if((person.arbeidsStatus in arrayOf("arbeidstaker","frilans","selvstendig"))){
            omsorgpenger=10L
        }

        person.barn?.forEach(){ it ->
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
        if(!(person.sosialStatus in arrayOf("gift","samboer"))){
            omsorgpenger*=2L
        }

        return omsorgpenger.toInt()
    }



    // oppdaterer database

    fun populateDataBaseFromJson(person:Person, database:HashMap<String?,PersonEntry?>){



        if(person == null){
            return
        }
        else{

            // komme seg unna ?
            ///val p = person.partner as List<Person>

            /*var dx = database as HashMap<String?,PersonEntry?>
            dx.containsKey(person.norskIdentitetsnummer)*/



            if(!database.containsKey(person.norskIdentitetsnummer)){
                /*person.partner?.forEach(){
                    println("--->"+it)
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær(it))
                }
                person.tidligerepartnere?.forEach(){
                    println("--->22"+it)
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær(it))
                }
                person.barn?.forEach(){
                    println("--->33"+it)
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær(it))
                }*/
                database[person?.norskIdentitetsnummer] = PersonEntry(person,regnutSamvær(person))
            }
            else{
                database[person?.norskIdentitetsnummer] = PersonEntry(person,regnutSamvær(person))
            }
        }


    }



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
    fun visomsorgspenger(@RequestParam(value = "norskIdentitetsnummer]", defaultValue = "") verdi: String):ResponseEntity<String>{
        return ResponseEntity("{\"omsorgspenger\" : "+omsorgspengerBeregningService.vis(verdi)+"}", null, HttpStatus.OK)
    }

/*
    @GetMapping
    @RequestMapping("/visomsorgspenger")
    fun omsorgspenger(@RequestParam(value = "personNummer", defaultValue = "") name: String) = omsorgspengerBeregningService.vis(norskIdentitetsnummer = )
*/


    @PostMapping(path = arrayOf("/recreq"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test2(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {

        try {
            val o: OmsorgspengerSøknad = OmsorgspengerSøknad.SerDes.deserialize(form)

            if(aldersbergning.bestemmeAlder(o.barn.fødselsdato)){

            }else{
                return ResponseEntity.status(409).build()
            }


            /*
            * 1. sjekke om brukeren finnes i databasen, eller sjekke om databasen er der i det heletatt...
            *
            * */

            /*
            println(o.barn.fødselsdato)
            val current = LocalDateTime.now()
            println(o.barn.fødselsdato.atStartOfDay())


            // fant ut om brukeren som ble sendt inn var under eller lik 12.

            var date = LocalDate.parse(o.barn.fødselsdato.toString())
            println(Period.between(o.barn.fødselsdato,current.toLocalDate()).years)
            */



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