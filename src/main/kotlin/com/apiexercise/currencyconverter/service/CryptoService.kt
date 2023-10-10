package com.apiexercise.currencyconverter.service

import com.apiexercise.currencyconverter.dto.BinanceDTO
import com.apiexercise.currencyconverter.dto.CoinbaseDTO
import com.apiexercise.currencyconverter.dto.CryptoOutputDTO
import com.apiexercise.currencyconverter.utils.CSVUtils
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class CryptoService {
    // Parse the ISO-4217 currencies CSV on application startup
    private lateinit var currencies: List<String>
    private val restTemplate = RestTemplate()

    init {
        val csvData = CSVUtils.parseCSV("src/main/resources/currencies.csv")
        currencies = csvData.mapNotNull { it["Currency code"] }
    }

    fun getExchangeRates(currency: String): CoinbaseDTO {
        val url = "https://api.coinbase.com/v2/exchange-rates?currency=$currency"
        return restTemplate.getForObject(url, CoinbaseDTO::class.java)!!
    }

    fun filterCryptoCurrencies(coinbaseDTO: CoinbaseDTO): List<String> {
        return coinbaseDTO.data.rates.keys.filter {
            it !in currencies
        }
    }

    fun getBinanceTickerData(): List<BinanceDTO> {
        val url = "https://api.binance.us/api/v3/ticker/24hr"
        val response = restTemplate.getForEntity(url, Array<BinanceDTO>::class.java)
        return response.body?.toList() ?: emptyList()
    }
    
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