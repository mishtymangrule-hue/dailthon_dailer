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
public final class InitiateCallUseCase_Factory implements Factory<InitiateCallUseCase> {
  private final Provider<DialerRepository> repositoryProvider;

  private final Provider<ValidatePhoneNumberUseCase> validatePhoneNumberProvider;

  public InitiateCallUseCase_Factory(Provider<DialerRepository> repositoryProvider,
      Provider<ValidatePhoneNumberUseCase> validatePhoneNumberProvider) {
    this.repositoryProvider = repositoryProvider;
    this.validatePhoneNumberProvider = validatePhoneNumberProvider;
  }

  @Override
  public InitiateCallUseCase get() {
    return newInstance(repositoryProvider.get(), validatePhoneNumberProvider.get());
  }

  public static InitiateCallUseCase_Factory create(
      javax.inject.Provider<DialerRepository> repositoryProvider,
      javax.inject.Provider<ValidatePhoneNumberUseCase> validatePhoneNumberProvider) {
    return new InitiateCallUseCase_Factory(Providers.asDaggerProvider(repositoryProvider), Providers.asDaggerProvider(validatePhoneNumberProvider));
  }

  public static InitiateCallUseCase_Factory create(Provider<DialerRepository> repositoryProvider,
      Provider<ValidatePhoneNumberUseCase> validatePhoneNumberProvider) {
    return new InitiateCallUseCase_Factory(repositoryProvider, validatePhoneNumberProvider);
  }

  public static InitiateCallUseCase newInstance(DialerRepository repository,
      ValidatePhoneNumberUseCase validatePhoneNumber) {
    return new InitiateCallUseCase(repository, validatePhoneNumber);
  }
}
