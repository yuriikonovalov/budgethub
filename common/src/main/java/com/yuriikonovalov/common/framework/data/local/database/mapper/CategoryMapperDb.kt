package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.framework.data.local.database.model.CategoryDb
import javax.inject.Inject

class CategoryMapperDb @Inject constructor() : MapperDb<CategoryDb, Category> {
    override fun mapToDomain(db: CategoryDb): Category {
        return Category(
            id = db.id,
            type = db.type,
            name = db.name,
            iconPath = db.iconPath,
            color = db.color,
            isCustom = db.isCustom
        )
    }

    override fun mapFromDomain(domain: Category): CategoryDb {
        return CategoryDb(
            id = domain.id,
            type = domain.type,
            name = domain.name,
            isCustom = domain.isCustom,
            iconPath = domain.iconPath,
            color = domain.color
        )
    }
}


