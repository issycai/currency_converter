package com.apiexercise.currencyconverter.dto

data class CoinbaseDTO(
        val data: CoinbaseData
)

data class CoinbaseData(
        val currency: String,
        val rates: Map<String,String>
)
