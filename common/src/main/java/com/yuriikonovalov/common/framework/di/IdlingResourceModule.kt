package com.yuriikonovalov.common.framework.di

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IdlingResourceModule {

    @Singleton
    @Provides
    fun provideIdlingResource(): EspressoIdlingResource = object : EspressoIdlingResource {
        private val RESOURCE = "GLOBAL"
        private val idlingResource = CountingIdlingResource(RESOURCE)
        override val instance: IdlingResource
            get() = idlingResource

        override fun increment() {
            idlingResource.increment()
        }

        override fun decrement() {
            if (!idlingResource.isIdleNow) {
                idlingResource.decrement()
            }
        }
    }
}