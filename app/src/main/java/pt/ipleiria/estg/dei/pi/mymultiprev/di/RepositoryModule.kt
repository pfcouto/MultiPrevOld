package pt.ipleiria.estg.dei.pi.mymultiprev.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.AuthDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.DrugDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.IntakeDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.PrescriptionItemDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.DrugNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.IntakeNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.PatientNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.PrescriptionItemNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.*

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        authDao: AuthDao,
        patientNetworkMapper: PatientNetworkMapper,
        sharedPreferencesRepository: SharedPreferencesRepository
    ) =
        AuthRepository(authDao, patientNetworkMapper, sharedPreferencesRepository)

    @Provides
    @Singleton
    fun providePrescriptionItemsRepository(
        prescriptionItemDao: PrescriptionItemDao,
        prescriptionItemNetworkMapper: PrescriptionItemNetworkMapper,
        sharedPreferencesRepository: SharedPreferencesRepository
    ) =
        PrescriptionItemsRepository(
            prescriptionItemDao,
            prescriptionItemNetworkMapper,
            sharedPreferencesRepository
        )

    @Provides
    @Singleton
    fun provideDrugsRepository(
        drugDao: DrugDao,
        drugNetworkMapper: DrugNetworkMapper,
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = DrugRepository(drugDao, drugNetworkMapper, sharedPreferencesRepository)

    @Provides
    @Singleton
    fun provideIntakesRepository(
        intakeDao: IntakeDao,
        intakeNetworkMapper: IntakeNetworkMapper,
        prescriptionItemDao: PrescriptionItemDao,
        sharedPreferencesRepository: SharedPreferencesRepository
    ) = IntakeRepository(
        prescriptionItemDao = prescriptionItemDao,
        intakeDao = intakeDao,
        intakeNetworkMapper = intakeNetworkMapper,
        sharedPreferencesRepository = sharedPreferencesRepository
    )

    @Provides
    @Singleton
    fun providesSharedPreferencesRepository(app: Application) = SharedPreferencesRepository(app)
}