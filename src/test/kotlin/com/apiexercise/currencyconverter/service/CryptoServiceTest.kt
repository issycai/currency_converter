package com.apiexercise.currencyconverter.service

import com.apiexercise.currencyconverter.dto.BinanceDTO
import com.apiexercise.currencyconverter.dto.CoinbaseDTO
import com.apiexercise.currencyconverter.dto.CoinbaseData
import com.apiexercise.currencyconverter.dto.CryptoOutputDTO
import com.apiexercise.currencyconverter.utils.CSVUtils
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class CryptoServiceTest {

    private lateinit var cryptoService: CryptoService

    @MockK
    private lateinit var mockRestTemplate: RestTemplate

    private lateinit var mockCurrencies: List<String>

    private val currency: String = "USD"

    private lateinit var mockCoinbaseResponse: CoinbaseDTO

    private lateinit var mockBinanceResponse: Array<BinanceDTO>

    @BeforeEach
    fun setUp() {
        cryptoService = CryptoService(mockRestTemplate)
        // mock importing currencies csv
        val csvData = CSVUtils.parseCSV("src/test/resources/currencies.csv")
        mockCurrencies = csvData.mapNotNull { it["Currency code"] }

        // mock Coinbase API response
        mockCoinbaseResponse = CoinbaseDTO(data = CoinbaseData(
                currency = currency,
                rates = mapOf("USD" to "1.0",
                        "BTC" to "60000.0",
                        "ETH" to "4000.0",
                        "ADA" to "2.0")
        ))
        every { mockRestTemplate.getForObject(any<String>(), CoinbaseDTO::class.java) } returns mockCoinbaseResponse

        // mock Binance API Response
        mockBinanceResponse = arrayOf(
                BinanceDTO(symbol = "BTCUSDT", priceChangePercent = "5.0"),
                BinanceDTO(symbol = "ETHBTC", priceChangePercent = "-2.0"),
        )
        every { mockRestTemplate.getForEntity(any<String>(), Array<BinanceDTO>::class.java) } returns ResponseEntity.ok(mockBinanceResponse)
    }

    @Test
    fun `getExchangeRates returns a list of exchange rates if given a currency`() {
        val result = cryptoService.getExchangeRates(currency)
        assertEquals(mockCoinbaseResponse, result)
    }

    @Test
    fun `filterCryptoCurrencies filters out the conventional currencies`() {
        val result = cryptoService.filterCryptoCurrencies(mockCoinbaseResponse)
        val expected = mockCoinbaseResponse.data.rates.keys.filter { it !in mockCurrencies }
        assertEquals(expected, result)
    }

    @Test
    fun `getBinanceTickerData should return a list of symbol and priceChangePercent`() {
        val result = cryptoService.getBinanceTickerData()
        assertEquals(mockBinanceResponse.toList(), result)
    }

    @Test
    fun `generateCryptoOutput outputs the cryptocurrency details based on the currency`() {
        val output = cryptoService.generateCryptoOutput(currency)

        val expectedOutput = listOf(
                CryptoOutputDTO(
                        cryptoCode = "ETH",
                        currentValueInChosenCurrency = BigDecimal("4000.000"),
                        percentChangeAgainstBTC = BigDecimal("-2.000"),
                        valueIfConvertedFromBTC = BigDecimal("3920.000"))
        )

        assertEquals(expectedOutput, output)
    }
}