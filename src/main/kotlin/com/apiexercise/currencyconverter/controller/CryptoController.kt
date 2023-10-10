package com.apiexercise.currencyconverter.controller

import com.apiexercise.currencyconverter.dto.CryptoOutputDTO
import com.apiexercise.currencyconverter.service.CryptoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller class responsible for handling cryptocurrency-related API endpoint
 */
@RestController
class CryptoController(private val cryptoService: CryptoService) {
    /**
     * Fetches a list of cryptocurrencies and their details based on the given currency
     *
     * @param currency The ISO-4217 currency code
     * @return A list of cryptocurrency details
     */
    @GetMapping("/cryptoOutput")
    fun getCryptoOutput(@RequestParam currency: String): ResponseEntity<List<CryptoOutputDTO>> {
        val data = cryptoService.generateCryptoOutput(currency)
        return ResponseEntity.ok(data)
    }
}