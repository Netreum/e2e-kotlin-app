package com.example.app

import Utils.Testrail.TestRailClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInfo

open class BaseTest {

    private val testRail = TestRailClient(
        baseUrl = "https://tallerautomationbackend.testrail.io/",
        username = "alvareznicolas1705@gmail.com",
        apiKey = "lEfvy.ttW9Hi74TsAZKi-swh3e2GgT205JrLeRXDp"
    )

    // Mapeo entre nombres de test y IDs de TestRail
    private val testCaseMap = mapOf(
        "Acceso a endpoint protegido con token valido()" to 4,
        "Envio de datos a endpoint protegido()" to 5,
        "Login con credenciales invalidas()" to 6,
        "Envio de datos con body vacio()" to 7,
        "Acceso a endpoint protegido sin token()" to 2
    )

    @AfterEach
    fun updateTestRailStatus(testInfo: TestInfo) {
        val testName = testInfo.displayName
        val caseId = testCaseMap[testName]

        if (caseId != null) {
            val success = testRail.addResultForCase(
                runId = 1, // ID del Test Run en TestRail
                caseId = caseId,
                statusId = 2, // 1 = Passed
                comment = "Test passed via automation"
            )
            if (!success) {
                println("Falló la actualización en TestRail para el test: $testName")
            }
        } else {
            println("No se encontró el ID de TestRail para el test: $testName")
        }
    }
}