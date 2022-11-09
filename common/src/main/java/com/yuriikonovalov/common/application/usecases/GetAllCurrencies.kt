package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency

interface GetAllCurrencies {
    operator fun invoke(): Resource<List<Currency>>
}