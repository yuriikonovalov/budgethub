package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency

interface SearchCurrency {
    operator fun invoke(query: String): Resource<List<Currency>>
}