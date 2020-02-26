package nav.omsorgspenger_sykt_barn

import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*


@RestController
class OmsorgspengerSyktBarnController {

    data class Person(val name: String) {
        var a: Int = 0
        val x: IntArray = intArrayOf(1, 2, 3)
    }



    fun handler(personNummer:String):Person{
        return Person(personNummer)
    }

    @GetMapping
    @RequestMapping("/")
    fun test(@RequestParam(value = "personNummer", defaultValue = "") name: String) = handler(name)

    @PostMapping(path = arrayOf("/recreq"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun test2(@RequestBody form: String, bindingResult: BindingResult) : String {
        val jsonObject = JSONObject(form)
        println(jsonObject.get("søker"))
        println(form)
        println(bindingResult)
        return "{}"
    }

}