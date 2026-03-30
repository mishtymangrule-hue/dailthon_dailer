package com.app.dialer.domain.usecase;

import com.app.dialer.domain.repository.DialerContactRepository;
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
public final class GetSuggestedContactsUseCase_Factory implements Factory<GetSuggestedContactsUseCase> {
  private final Provider<DialerContactRepository> contactRepositoryProvider;

  public GetSuggestedContactsUseCase_Factory(
      Provider<DialerContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  @Override
  public GetSuggestedContactsUseCase get() {
    return newInstance(contactRepositoryProvider.get());
  }

  public static GetSuggestedContactsUseCase_Factory create(
      javax.inject.Provider<DialerContactRepository> contactRepositoryProvider) {
    return new GetSuggestedContactsUseCase_Factory(Providers.asDaggerProvider(contactRepositoryProvider));
  }

  public static GetSuggestedContactsUseCase_Factory create(
      Provider<DialerContactRepository> contactRepositoryProvider) {
    return new GetSuggestedContactsUseCase_Factory(contactRepositoryProvider);
  }

  public static GetSuggestedContactsUseCase newInstance(DialerContactRepository contactRepository) {
    return new GetSuggestedContactsUseCase(contactRepository);
  }
}
