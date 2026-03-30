package com.app.dialer.domain.usecase;

import com.app.dialer.domain.repository.DialerRepository;
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
public final class GetAvailableSimCardsUseCase_Factory implements Factory<GetAvailableSimCardsUseCase> {
  private final Provider<DialerRepository> repositoryProvider;

  public GetAvailableSimCardsUseCase_Factory(Provider<DialerRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetAvailableSimCardsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetAvailableSimCardsUseCase_Factory create(
      javax.inject.Provider<DialerRepository> repositoryProvider) {
    return new GetAvailableSimCardsUseCase_Factory(Providers.asDaggerProvider(repositoryProvider));
  }

  public static GetAvailableSimCardsUseCase_Factory create(
      Provider<DialerRepository> repositoryProvider) {
    return new GetAvailableSimCardsUseCase_Factory(repositoryProvider);
  }

  public static GetAvailableSimCardsUseCase newInstance(DialerRepository repository) {
    return new GetAvailableSimCardsUseCase(repository);
  }
}
