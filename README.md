# API Integration Exercise

## Goal

The goal of this project is to create a Spring Boot application that allows users to retrieve information about
cryptocurrencies and their performance against Bitcoin (BTC) over the last 24 hours. The application integrates with
external APIs from Coinbase and Binance to gather this data.

## Output

The application provides the following information for cryptocurrencies:

+ The cryptocurrency code (e.g., ETH, ADA, SOL).
+ The current value of the cryptocurrency in the chosen currency.
+ What percent the cryptocurrency has gained/lost against Bitcoin (BTC) in the last 24 hours.
+ Optionally, how much money you would have right now if you had converted one Bitcoin (BTC) 24 hours ago to that
  currency.

## How to Run

To run the project locally, follow these steps:

1. Clone the Repository:
   `git clone git@github.com:issycai/currency_converter.git`

2. Build the Project:

```shell
cd currency_converter
./gradlew build
```

3. Run the Application:

Use the following command to start the Spring Boot application:

```shell
./gradlew bootRun
```

4. Access the API:

Once the application is running, you can access the API at (either from a web browser or Postman GET):

```shell
http://localhost:8080/cryptoOutput?currency=USD
```

Replace USD with the desired ISO-4217 currency code (e.g., EUR, GBP, JPY) to retrieve cryptocurrency information in that
currency.

## Example Output

Here's an example of the output you can expect:

```json
[
  {
    "cryptoCode": "ETH",
    "currentValue": 3420.123,
    "percentChangeAgainstBTC": -2.560,
    "valueIfConverted": 0.324
  },
  {
    "cryptoCode": "ADA",
    "currentValue": 2.345,
    "percentChangeAgainstBTC": 1.780,
    "valueIfConverted": 0.025
  },
  {
    "cryptoCode": "SOL",
    "currentValue": 156.789,
    "percentChangeAgainstBTC": 5.920,
    "valueIfConverted": 0.195
  }
]

```

## Dependencies

+ Kotlin
+ Spring Boot
+ RestTemplate
+ MockK (for testing)
