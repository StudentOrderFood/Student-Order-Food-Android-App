package prm392.orderfood.data.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import prm392.orderfood.data.repositoryImpl.AuthRepositoryImpl;
import prm392.orderfood.data.repositoryImpl.ShopRepositoryImpl;
import prm392.orderfood.data.repositoryImpl.UserRepositoryImpl;
import prm392.orderfood.domain.repositories.AuthRepository;
import prm392.orderfood.domain.repositories.ShopRepository;
import prm392.orderfood.domain.repositories.UserRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    @Binds
    @Singleton
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract UserRepository bindUserRepository(UserRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract ShopRepository bindShopRepository(ShopRepositoryImpl impl);
}
