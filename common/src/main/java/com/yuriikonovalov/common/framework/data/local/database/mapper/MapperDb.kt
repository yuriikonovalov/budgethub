package com.yuriikonovalov.common.framework.data.local.database.mapper

interface MapperDb<Db, Domain> {
    fun mapToDomain(db: Db): Domain
    fun mapFromDomain(domain: Domain): Db
}