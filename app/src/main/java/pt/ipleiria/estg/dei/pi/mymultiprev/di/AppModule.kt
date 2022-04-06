package pt.ipleiria.estg.dei.pi.mymultiprev.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.MultiPrevDatabase
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.*
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(app: Application): MultiPrevDatabase =
        Room.databaseBuilder(app, MultiPrevDatabase::class.java, Constants.LOCAL_DATABASE_NAME)
            .build()

    @Provides
    fun providePrescriptionItemsDao(multiPrevDatabase: MultiPrevDatabase): PrescriptionItemDao =
        multiPrevDatabase.prescriptionItemDao()

    @Provides
    fun provideDrugDao(multiPrevDatabase: MultiPrevDatabase): DrugDao = multiPrevDatabase.drugDao()

    @Provides
    fun provideIntakeDao(multiPrevDatabase: MultiPrevDatabase): IntakeDao =
        multiPrevDatabase.intakeDao()

    @Provides
    fun provideAuthDao(multiPrevDatabase: MultiPrevDatabase): AuthDao = multiPrevDatabase.authDao()

    @Provides
    fun provideAlarmDao(multiPrevDatabase: MultiPrevDatabase): AlarmDao =
        multiPrevDatabase.alarmDao()
}