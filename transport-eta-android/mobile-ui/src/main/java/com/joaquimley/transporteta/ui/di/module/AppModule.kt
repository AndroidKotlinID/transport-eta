package com.joaquimley.transporteta.ui.di.module

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.joaquimley.transporteta.ui.di.component.SmsControllerSubComponent
import com.joaquimley.transporteta.ui.injection.scope.PerApplication
import dagger.Module
import dagger.Provides

/**
 * Module used to provide dependencies at an application-level.
 */
@Module(subcomponents = [
    SmsControllerSubComponent::class
])
class AppModule {

    @Provides
    @PerApplication
    fun provideContext(app: Application): Context {
        return app
    }

    @Provides
    @PerApplication
    internal fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelProvider.NewInstanceFactory()
    }
}