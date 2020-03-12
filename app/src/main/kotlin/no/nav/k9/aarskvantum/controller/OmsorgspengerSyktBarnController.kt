package no.nav.k9.aarskvantum.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.k9.aarskvantum.service.Aldersbergning
import no.nav.k9.søknad.JsonUtils
import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

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
        val arbeidsStatus:String?
) {
    /*var age: Int = 0
    var idnummer:String= "";
    var barn: List<Person> = emptyList()
    var foreldre:List<Person> = emptyList()
    var partnere:List<Person> = emptyList()
    var bosted:String=""
    var medlemskap = ""
    var sosialstatus=""
    var arbeidsstatus=""*/
}

data class TestModel(
        val id: Int,
        val navn:String
)



@RestController
class OmsorgspengerSyktBarnController(val aldersbergning: Aldersbergning) {


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

    @GetMapping
    @RequestMapping("/listpartner")
    fun listpartner(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "---<"+listpartners(name)
    
    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = "hello"

    fun regnutSamvær():Int{
        return 10
    }

    // oppdaterer database

    fun populateDataBaseFromJson(person:Person, database:HashMap<String?,PersonEntry?>){
        if(person == null){
            return
        }
        else{
            if(!database.containsKey(person.norskIdentitetsnummer)){
                person.partner?.forEach(){
                    println(it)
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær())
                }
                person.tidligerepartnere?.forEach(){
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær())
                }
                person.barn?.forEach(){
                    populateDataBaseFromJson(it,database)
                    database[it?.norskIdentitetsnummer] = PersonEntry(it,regnutSamvær())
                }
                database[person?.norskIdentitetsnummer] = PersonEntry(person,regnutSamvær())
            }
        }


    }

    @PostMapping(path = arrayOf("/test"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test3(@RequestBody form: String, bindingResult: BindingResult) : ResponseEntity<Unit> {

        val mapper = jacksonObjectMapper()
        /*var json = "{\"id\":42}"
        var t:TestModel = mapper.readValue<TestModel>(json)*/

        //var t2:TestModel = mapper.readValue<TestModel>(form)

        /*
        var fromApp:Person = mapper.readValue<Person>(form)
        println(fromApp.norskIdentitetsnummer)
        println(fromApp.norskIdentitetsnummer)
        */
        //var barn:Person = Person("76767",null)
        var olejakob:Person = Person("190215XXXXX",null,null,null,null,null,null,null,null)
        var heidi:Person = Person("190215XXXXX",null,null,null,null,null,null,null,null)
        //var person:Person = Person("76767",barn)
        //var list: List<Person> = listOf(barn,barn)
        //var personBarn:Person = Person("76767",list)
        //println(mapper.writeValueAsString(personBarn))
        var person:Person = Person("26067639501",listOf(olejakob),null,listOf(heidi),null,"Oslo","Folketrygd","gift","arbeidstaker")

        //var TestModel = JsonUtils.fromString("{\"id\":42}",TestModel::class.java) as TestModel
        //var TestModel = JsonUtils.fromString(form,TestModel::class.java) as TestModel
        //val person = JsonUtils.fromString(form, Person::class.java) as Person
        //OmsorgspengerSøknad.SerDes.deserialize(form)
        println(form)
        //println(t.id)


        println(mapper.writeValueAsString(person))


        /*var fuck:Person = mapper.readValue<Person>("{\"norskIdentitetsnummer\":\"26067639501\",\"barn\":{\"norskIdentitetsnummer\":\"2606763950100000\"}}")
        println(fuck.barn?.norskIdentitetsnummer)*/

        var suck:Person = mapper.readValue<Person>(form)
        println(suck.norskIdentitetsnummer)

        populateDataBaseFromJson(suck,database)
        println("*****>"+database.keys.size)

        /*
        var personX:PersonEntry = PersonEntry(suck,10)
        database[suck.norskIdentitetsnummer]=personX
        println(database.keys.size)
        */

        // remmeber test for invalid json
        /*println(t2.id)
        println(t2.navn)*/

        /*var fromApp:Person = mapper.readValue<Person>(form)
        println(fromApp.barn?.size)*/

        return ResponseEntity.ok().build()
    }



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