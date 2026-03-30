package com.app.dialer.data.repository;

import com.app.dialer.data.local.CallLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CallLogRepositoryImpl_Factory implements Factory<CallLogRepositoryImpl> {
  private final Provider<CallLogDao> daoProvider;

  public CallLogRepositoryImpl_Factory(Provider<CallLogDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CallLogRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static CallLogRepositoryImpl_Factory create(
      javax.inject.Provider<CallLogDao> daoProvider) {
    return new CallLogRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider));
  }

  public static CallLogRepositoryImpl_Factory create(Provider<CallLogDao> daoProvider) {
    return new CallLogRepositoryImpl_Factory(daoProvider);
  }

  public static CallLogRepositoryImpl newInstance(CallLogDao dao) {
    return new CallLogRepositoryImpl(dao);
  }
}
