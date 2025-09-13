package Utils.Testrail

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

class TestRailClient(
        private val baseUrl: String,
        private val username: String,
        private val apiKey: String
) {
    private val client = HttpClient.newBuilder().build()
    private val authHeader = "Basic " + Base64.getEncoder()
            .encodeToString("$username:$apiKey".toByteArray())

    fun addResultForCase(runId: Int, caseId: Int, statusId: Int, comment: String = ""): Boolean {
        val url = "$baseUrl/index.php?/api/v2/add_result_for_case/$runId/$caseId"
        val body = """
            {
                "status_id": $statusId,
                "comment": "$comment"
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("TestRail response: ${response.statusCode()} - ${response.body()}")
        return response.statusCode() == 200
    }
}