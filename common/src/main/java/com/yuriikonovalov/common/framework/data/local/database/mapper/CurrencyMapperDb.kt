package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.framework.data.local.system.CurrencyDetails
import javax.inject.Inject

class CurrencyMapperDb @Inject constructor() : MapperDb<String, Currency> {

    override fun mapToDomain(db: String): Currency {
        return Currency(
            code = db,
            name = CurrencyDetails.getName(db),
            symbol = CurrencyDetails.getSymbol(db)
        )
    }

    override fun mapFromDomain(domain: Currency): String {
        return domain.code
    }
}