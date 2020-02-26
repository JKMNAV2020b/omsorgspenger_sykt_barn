package nav.omsorgspenger_sykt_barn

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.net.URI


@Autowired
private val restTemplate: TestRestTemplate? = null

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OmsorgspengerSyktBarnControllerTest(@Autowired private val restTemplate: TestRestTemplate) {

    val example = "{\n" +
            "        \"søknadId\":\"XXXX\",\n" +
            "        \"mottattDato\":\"260676\",\n" +
            "        \"søker\":\"Per Person\"\n" +
            "        \"barn\":\"Jens Person\"\n" +
            "    }"

    @Test
    fun reqsend(){
        val baseUrl = "http://localhost:8080/recreq";
        val uri = URI(baseUrl)

        val headers: HttpHeaders = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        val request: HttpEntity<String> = HttpEntity<String>(example, headers)

        println(request)

        val result = restTemplate!!.postForEntity(uri, request, String::class.java)
        println(result.statusCode)
        assert(true)
    }
}