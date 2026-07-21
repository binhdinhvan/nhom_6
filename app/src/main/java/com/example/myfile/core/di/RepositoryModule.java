package com.example.myfile.core.di;

import com.example.myfile.core.repository.DefaultFileTypeResolver;
import com.example.myfile.core.repository.FileRepository;
import com.example.myfile.core.repository.FileTypeResolver;
import com.example.myfile.core.repository.LocalFileRepositoryImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * DEPENDENCY INJECTION.
 *
 * Doi implementation chi can sua 1 dong o day -> toan bo app dung ban moi.
 * VD: khi C viet MimeUtilsTypeResolver, doi @Binds cua bindTypeResolver.
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract FileRepository bindFileRepository(LocalFileRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract FileTypeResolver bindTypeResolver(DefaultFileTypeResolver impl);
}
