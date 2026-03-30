package com.app.dialer.core.di;

import com.app.dialer.data.local.AppDatabase;
import com.app.dialer.data.local.CallLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideCallLogDaoFactory implements Factory<CallLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideCallLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CallLogDao get() {
    return provideCallLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCallLogDaoFactory create(
      javax.inject.Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideCallLogDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideCallLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideCallLogDaoFactory(dbProvider);
  }

  public static CallLogDao provideCallLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCallLogDao(db));
  }
}
