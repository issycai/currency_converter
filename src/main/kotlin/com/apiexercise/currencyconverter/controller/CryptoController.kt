package com.apiexercise.currencyconverter.controller

import com.apiexercise.currencyconverter.dto.CoinbaseDTO
import com.apiexercise.currencyconverter.dto.CryptoOutputDTO
import com.apiexercise.currencyconverter.service.CryptoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CryptoController(private val cryptoService: CryptoService) {
    @GetMapping("/exchangeRates")
    fun getExchangeRates(@RequestParam currency: String): ResponseEntity<List<String>> {
        val coinbaseData = cryptoService.getExchangeRates(currency)
        val cryptoCurrencies = cryptoService.filterCryptoCurrencies(coinbaseData)
        return ResponseEntity.ok(cryptoCurrencies)
    }

    @GetMapping("/cryptoOutput")
    fun getCryptoOutput(@RequestParam currency: String): ResponseEntity<List<CryptoOutputDTO>> {
        val data = cryptoService.generateCryptoOutput(currency)
        return ResponseEntity.ok(data)
    }
}