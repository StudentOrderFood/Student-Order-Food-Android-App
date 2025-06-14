package prm392.orderfood.data.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import prm392.orderfood.data.repositoryImpl.auth.AuthRepositoryImpl;
import prm392.orderfood.domain.repositories.auth.AuthRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    @Binds
    @Singleton
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl impl);
}
