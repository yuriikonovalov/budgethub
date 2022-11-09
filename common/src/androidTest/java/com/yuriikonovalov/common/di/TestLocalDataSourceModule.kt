//package com.yuriikonovalov.common.di
//
//import com.yuriikonovalov.common.data.local.*
//import com.yuriikonovalov.common.fake.*
//import com.yuriikonovalov.common.framework.data.local.assets.IconsLocalDataSourceImpl
//import com.yuriikonovalov.common.framework.data.local.database.datasources.*
//import com.yuriikonovalov.common.framework.data.local.file.DatabaseBackupLocalDataSourceImpl
//import com.yuriikonovalov.common.framework.data.local.file.TransactionImagesLocalDataSourceImpl
//import com.yuriikonovalov.common.framework.data.local.system.CurrenciesLocalDataSourceImpl
//import com.yuriikonovalov.common.framework.data.local.system.CurrencyDetails
//import com.yuriikonovalov.common.framework.di.LocalDataSourceModule
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.components.SingletonComponent
//import dagger.hilt.testing.TestInstallIn
//
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [LocalDataSourceModule::class]
//)
//abstract class TestLocalDataSourceModule {
//
//    @Binds
//    abstract fun bindCurrencyDetails(impl: CurrencyDetails): CurrencyDetails
//
//    @Binds
//    abstract fun bindTransactionsLocalDataSource(impl: TransactionsLocalDataSourceImpl): TransactionsLocalDataSource
//
//    @Binds
//    abstract fun bindAccountsLocalDataSource(impl: AccountsLocalDataSourceImpl): AccountsLocalDataSource
//
//    @Binds
//    abstract fun bindTagsLocalDataSource(impl: TagsLocalDataSourceImpl): TagsLocalDataSource
//
//    @Binds
//    abstract fun bindCategoriesLocalDataSource(impl: CategoriesLocalDataSourceImpl): CategoriesLocalDataSource
//
//    @Binds
//    abstract fun bindTransfersLocalDataSource(impl: TransfersLocalDataSourceImpl): TransfersLocalDataSource
//
//    @Binds
//    abstract fun bindCurrenciesLocalDataSource(impl: CurrenciesLocalDataSourceImpl): CurrenciesLocalDataSource
//
//    @Binds
//    abstract fun bindTransactionImagesLocalDataSource(impl: TransactionImagesLocalDataSourceImpl): TransactionImagesLocalDataSource
//
//    @Binds
//    abstract fun bindIconsLocalDataSource(impl: IconsLocalDataSourceImpl): IconsLocalDataSource
//
//    @Binds
//    abstract fun bindDatabaseBackupLocalDataSource(impl: DatabaseBackupLocalDataSourceImpl): DatabaseBackupLocalDataSource
//}
//
//
////package com.yuriikonovalov.common.di
////
////import com.yuriikonovalov.common.data.local.*
////import com.yuriikonovalov.common.fake.*
////import com.yuriikonovalov.common.framework.data.local.assets.IconsLocalDataSourceImpl
////import com.yuriikonovalov.common.framework.data.local.database.datasources.*
////import com.yuriikonovalov.common.framework.data.local.file.DatabaseBackupLocalDataSourceImpl
////import com.yuriikonovalov.common.framework.data.local.system.CurrenciesLocalDataSourceImpl
////import com.yuriikonovalov.common.framework.data.local.system.CurrencyDetails
////import com.yuriikonovalov.common.framework.di.LocalDataSourceModule
////import dagger.Binds
////import dagger.Module
////import dagger.hilt.components.SingletonComponent
////import dagger.hilt.testing.TestInstallIn
////
////
////@Module
////@TestInstallIn(
////    components = [SingletonComponent::class],
////    replaces = [LocalDataSourceModule::class]
////)
////abstract class TestLocalDataSourceModule {
////
////    @Binds
////    abstract fun bindCurrencyDetails(impl: CurrencyDetails): CurrencyDetails
////
////    @Binds
////    abstract fun bindTransactionsLocalDataSource(impl: FakeTransactionsLocalDataSource): TransactionsLocalDataSource
////
////    @Binds
////    abstract fun bindAccountsLocalDataSource(impl: FakeAccountsLocalDataSource): AccountsLocalDataSource
////
////    @Binds
////    abstract fun bindTagsLocalDataSource(impl: FakeTagsLocalDataSource): TagsLocalDataSource
////
////    @Binds
////    abstract fun bindCategoriesLocalDataSource(impl: FakeCategoriesLocalDataSource): CategoriesLocalDataSource
////
////    @Binds
////    abstract fun bindTransfersLocalDataSource(impl: FakeTransfersLocalDataSource): TransfersLocalDataSource
////
////    @Binds
////    abstract fun bindCurrenciesLocalDataSource(impl: CurrenciesLocalDataSourceImpl): CurrenciesLocalDataSource
////
////    @Binds
////    abstract fun bindTransactionImagesLocalDataSource(impl: FakeTransactionImagesLocalDataSource): TransactionImagesLocalDataSource
////
////    @Binds
////    abstract fun bindIconsLocalDataSource(impl: IconsLocalDataSourceImpl): IconsLocalDataSource
////
////    @Binds
////    abstract fun bindDatabaseBackupLocalDataSource(impl: DatabaseBackupLocalDataSourceImpl): DatabaseBackupLocalDataSource
////}
