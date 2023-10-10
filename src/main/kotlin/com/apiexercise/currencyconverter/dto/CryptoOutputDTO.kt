package com.apiexercise.currencyconverter.dto

import java.math.BigDecimal

data class CryptoOutputDTO(
        val cryptoCode: String,
        val currentValueInChosenCurrency: BigDecimal,
        val percentChangeAgainstBTC: BigDecimal,
        val valueIfConvertedFromBTC: BigDecimal? // optional
)
