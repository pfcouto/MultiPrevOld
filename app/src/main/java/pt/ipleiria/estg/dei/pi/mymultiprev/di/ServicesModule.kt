package pt.ipleiria.estg.dei.pi.mymultiprev.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipleiria.estg.dei.pi.mymultiprev.service.AlarmService

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    @Provides
    fun provideAlarmService(app: Application) = AlarmService(app)
}