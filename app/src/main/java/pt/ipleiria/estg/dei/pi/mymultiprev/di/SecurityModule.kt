package pt.ipleiria.estg.dei.pi.mymultiprev.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipleiria.estg.dei.pi.mymultiprev.crypto.SHA3Util
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideSHA3Util() = SHA3Util()
}