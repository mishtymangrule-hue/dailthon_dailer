package com.app.dialer.domain.usecase;

import com.app.dialer.domain.repository.CallLogRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ObserveCallLogUseCase_Factory implements Factory<ObserveCallLogUseCase> {
  private final Provider<CallLogRepository> repositoryProvider;

  public ObserveCallLogUseCase_Factory(Provider<CallLogRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveCallLogUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveCallLogUseCase_Factory create(
      javax.inject.Provider<CallLogRepository> repositoryProvider) {
    return new ObserveCallLogUseCase_Factory(Providers.asDaggerProvider(repositoryProvider));
  }

  public static ObserveCallLogUseCase_Factory create(
      Provider<CallLogRepository> repositoryProvider) {
    return new ObserveCallLogUseCase_Factory(repositoryProvider);
  }

  public static ObserveCallLogUseCase newInstance(CallLogRepository repository) {
    return new ObserveCallLogUseCase(repository);
  }
}
