package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.assertThat
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeGetAllCurrenciesSource
import org.junit.Before
import org.junit.Test

class SearchCurrencyTest {
    private val currencies = listOf(
        Currency("USD", "Dollar", "$"),
        Currency("UAH", "Ukrainian hryvnia", "₴"),
        Currency("EUR", "Euro", "€"),
        Currency("NOK", "Norwegian krone", "kr")
    )
    private lateinit var sut: SearchCurrencyImpl

    @Before
    fun setup() {
        val source = FakeGetAllCurrenciesSource(currencies)
        val getAllCurrencies = GetAllCurrenciesImpl(source)
        sut = SearchCurrencyImpl(getAllCurrencies)
    }

    @Test
    fun `Input the code of an available currency returns the exact currency`() {
        // BEFORE
        val expected = "USD"

        // WHEN
        val actual = (sut(expected) as Resource.Success).data.first().code

        // THEN
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Input characters of the name of an available currency returns currencies with the characters in their names`() {
        // BEFORE
        val query = "ian"

        // WHEN
        val result = sut(query) as Resource.Success

        // THEN
        // Norweg|ian| krone - NOK and Ukrain|ian| hryvnia - UAH
        val expected = listOf("NOK", "UAH")
        val actual = result.data.map { it.code }
        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `Input a not existing query returns empty result`() {
        // BEFORE
        val query = "DKK"

        // WHEN
        val actual = sut(query)

        // THEN
        // There's no currencies that contain the query characters.
        assertThat(actual).isInstanceOf(Resource.Failure::class.java)
    }
}