package com.app.dialer.domain.usecase;

import com.app.dialer.domain.repository.ContactRepository;
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
public final class SearchContactsUseCase_Factory implements Factory<SearchContactsUseCase> {
  private final Provider<ContactRepository> repositoryProvider;

  public SearchContactsUseCase_Factory(Provider<ContactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SearchContactsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SearchContactsUseCase_Factory create(
      javax.inject.Provider<ContactRepository> repositoryProvider) {
    return new SearchContactsUseCase_Factory(Providers.asDaggerProvider(repositoryProvider));
  }

  public static SearchContactsUseCase_Factory create(
      Provider<ContactRepository> repositoryProvider) {
    return new SearchContactsUseCase_Factory(repositoryProvider);
  }

  public static SearchContactsUseCase newInstance(ContactRepository repository) {
    return new SearchContactsUseCase(repository);
  }
}
