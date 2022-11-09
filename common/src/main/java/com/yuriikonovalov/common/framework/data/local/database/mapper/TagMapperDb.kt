package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import javax.inject.Inject

class TagMapperDb @Inject constructor() : MapperDb<TagDb, Tag> {
    override fun mapToDomain(db: TagDb): Tag {
        return Tag(
            id = db.id,
            name = db.name,
        )
    }

    override fun mapFromDomain(domain: Tag): TagDb {
        return TagDb(
            id = domain.id,
            name = domain.name,
        )
    }
}