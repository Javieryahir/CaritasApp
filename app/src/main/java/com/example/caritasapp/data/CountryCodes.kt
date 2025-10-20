package com.example.caritasapp.data

data class CountryCode(
    val name: String,
    val code: String,
    val flag: String
)

val countryCodes = listOf(
    CountryCode("México", "+52", "🇲🇽"),
    CountryCode("Estados Unidos", "+1", "🇺🇸"),
    CountryCode("Canadá", "+1", "🇨🇦"),
    CountryCode("España", "+34", "🇪🇸"),
    CountryCode("Argentina", "+54", "🇦🇷"),
    CountryCode("Colombia", "+57", "🇨🇴"),
    CountryCode("Chile", "+56", "🇨🇱"),
    CountryCode("Perú", "+51", "🇵🇪"),
    CountryCode("Venezuela", "+58", "🇻🇪"),
    CountryCode("Brasil", "+55", "🇧🇷"),
    CountryCode("Guatemala", "+502", "🇬🇹"),
    CountryCode("Honduras", "+504", "🇭🇳"),
    CountryCode("El Salvador", "+503", "🇸🇻"),
    CountryCode("Nicaragua", "+505", "🇳🇮"),
    CountryCode("Costa Rica", "+506", "🇨🇷"),
    CountryCode("Panamá", "+507", "🇵🇦"),
    CountryCode("República Dominicana", "+1", "🇩🇴"),
    CountryCode("Cuba", "+53", "🇨🇺"),
    CountryCode("Puerto Rico", "+1", "🇵🇷"),
    CountryCode("Ecuador", "+593", "🇪🇨"),
    CountryCode("Bolivia", "+591", "🇧🇴"),
    CountryCode("Paraguay", "+595", "🇵🇾"),
    CountryCode("Uruguay", "+598", "🇺🇾"),
    CountryCode("Francia", "+33", "🇫🇷"),
    CountryCode("Alemania", "+49", "🇩🇪"),
    CountryCode("Italia", "+39", "🇮🇹"),
    CountryCode("Reino Unido", "+44", "🇬🇧")
)




