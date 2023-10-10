package com.apiexercise.currencyconverter.service

import com.apiexercise.currencyconverter.dto.BinanceDTO
import com.apiexercise.currencyconverter.dto.CoinbaseDTO
import com.apiexercise.currencyconverter.dto.CryptoOutputDTO
import com.apiexercise.currencyconverter.utils.CSVUtils
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Service class responsible for handling cryptocurrency-related operations
 */

@Service
class CryptoService(private val restTemplate: RestTemplate) {
    private lateinit var currencies: List<String>

    init {
        val csvData = CSVUtils.parseCSV("src/main/resources/currencies.csv")
        currencies = csvData.mapNotNull { it["Currency code"] }
    }

    /**
     * Fetches the current exchange rates for the given currency from Coinbase API
     * @param currency The ISO-4217 currency code
     * @return The exchange rates data from Coinbase
     */
    fun getExchangeRates(currency: String): CoinbaseDTO {
        val url = "https://api.coinbase.com/v2/exchange-rates?currency=$currency"
        return restTemplate.getForObject(url, CoinbaseDTO::class.java)!!
    }

    /**
     * Filters out conventional currencies from the given Coinbase data, leaving only cryptocurrencies
     *
     * @param coinbaseDTO The exchange rates data from Coinbase
     * @return A list of cryptocurrency codes
     */
    fun filterCryptoCurrencies(coinbaseDTO: CoinbaseDTO): List<String> {
        return coinbaseDTO.data.rates.keys.filter {
            it !in currencies
        }
    }

    /**
     * Fetches the ticker data from Binance 24 hour ticker API
     * @return A list of symbol and priceChangePercent data from Binance
     */
    fun getBinanceTickerData(): List<BinanceDTO> {
        val url = "https://api.binance.us/api/v3/ticker/24hr"
        val response = restTemplate.getForEntity(url, Array<BinanceDTO>::class.java)
        return response.body?.toList() ?: emptyList()
    }

    /**
     * Generates a list of cryptocurrency details based on the given currency. The details include:
     * - Cryptocurrency code (e.g. ETH)
     * - Current value in the chosen currency
     * - Percentage change against Bitcoin in the last 24 hours
     * - (Optional) Value if one bitcoin had been converted to the cryptocurrency 24 hours ago
     *
     * @param chosenCurrency The ISO-4217 currency code chosen by the user
     * @return A list of cryptocurrency details
     */
    fun generateCryptoOutput(chosenCurrency: String): List<CryptoOutputDTO> {
        val coinbaseData = getExchangeRates(chosenCurrency)
        val cryptoCurrencies = filterCryptoCurrencies(coinbaseData)
        val binanceData = getBinanceTickerData()

        return cryptoCurrencies.mapNotNull { crypto ->
            val currentValue = coinbaseData.data.rates[crypto]?.toBigDecimal()?.setScale(3, RoundingMode.HALF_UP)
            val binanceTicker = binanceData.find { it.symbol == "${crypto}BTC" }

            if (currentValue != null && binanceTicker != null) {
                val percentChange = binanceTicker.priceChangePercent.toBigDecimal().setScale(3, RoundingMode.HALF_UP)
                val valueIfConverted = currentValue.multiply(BigDecimal(1 + percentChange.toDouble() / 100)).setScale(3, RoundingMode.HALF_UP)
                CryptoOutputDTO(crypto, currentValue, percentChange, valueIfConverted)
            } else {
                null
            }
        }
    }

}