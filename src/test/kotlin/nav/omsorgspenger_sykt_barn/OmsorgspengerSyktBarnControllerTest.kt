package nav.omsorgspenger_sykt_barn

import com.google.gson.Gson
import no.nav.k9.søknad.omsorgspenger.OmsorgspengerSøknad
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.net.URI


/*@Autowired
private val restTemplate: TestRestTemplate? = null*/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OmsorgspengerSyktBarnControllerTest(@Autowired private val restTemplate: TestRestTemplate) {

    @Test
    fun korrekt_json(){
        var eksempel = """
            {
              "søknadId": "123-123-123",
              "mottattDato": "2019-10-20T07:15:36.124Z",
              "versjon": "0.0.1",
              "søker": {
                "norskIdentitetsnummer": "11111111111"
              },
              "barn": {
                "fødselsdato": "2015-01-01",
                "norskIdentitetsnummer" : null
              }
            }
        """.trimIndent()
        val baseUrl = "http://localhost:8080/recreq"
        val uri = URI(baseUrl)
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        val request: HttpEntity<String> = HttpEntity<String>(eksempel, headers)
        val result = restTemplate.postForEntity(uri, request, String::class.java)

        assert(result.statusCodeValue == 200)
    }

    @Test
    fun misformet_json_skal_stoppe(){
        val example = "{\"søknadId\":\"XXXX\",\"mottattDato\":\"260676\",\"søker\":\"Per Person\"\"barn\":\"Jens Person\""
        val baseUrl = "http://localhost:8080/recreq"
        val uri = URI(baseUrl)
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        val request: HttpEntity<String> = HttpEntity<String>(example, headers)
        val result = restTemplate.postForEntity(uri, request, String::class.java)
        assert(result.statusCodeValue == 500)
    }

    @Test
    fun feiler_med_GSON(){

        var eksempelSøknad = """{"søknadId":"123-123-123","mottattDato":"2019-10-20T07:15:36.124Z","versjon":"0.0.1","søker":{"norskIdentitetsnummer":"11111111111"},"barn":{"fødselsdato":"2015-01-01","norskIdentitetsnummer":"null"}}"""

        assertThrows<com.google.gson.JsonSyntaxException>{
            Gson().fromJson(eksempelSøknad,OmsorgspengerSøknad::class.java)
        }
        //should continue to investigate why this is a problem....even though it is not a relevant problem
        assertDoesNotThrow {
            eksempelSøknad = eksempelSøknad.replace("ø","oe")
            println(eksempelSøknad)
            var omsorgspengerSøknad = Gson().fromJson("""{"soeknadId":"123-123-123","mottattDato":"2019"}""",OmsorgspengerSøknad::class.java)
        }
    }
}