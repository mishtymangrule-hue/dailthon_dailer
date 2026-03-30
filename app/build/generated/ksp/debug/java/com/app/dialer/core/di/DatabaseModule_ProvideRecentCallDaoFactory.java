package com.app.dialer.core.di;

import com.app.dialer.data.local.AppDatabase;
import com.app.dialer.data.local.RecentCallDao;
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
public final class DatabaseModule_ProvideRecentCallDaoFactory implements Factory<RecentCallDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideRecentCallDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RecentCallDao get() {
    return provideRecentCallDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRecentCallDaoFactory create(
      javax.inject.Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideRecentCallDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideRecentCallDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideRecentCallDaoFactory(dbProvider);
  }

  public static RecentCallDao provideRecentCallDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRecentCallDao(db));
  }
}
