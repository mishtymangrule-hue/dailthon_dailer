package com.app.dialer.data.repository;

import com.app.dialer.data.local.ContactDao;
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
public final class ContactRepositoryImpl_Factory implements Factory<ContactRepositoryImpl> {
  private final Provider<ContactDao> daoProvider;

  public ContactRepositoryImpl_Factory(Provider<ContactDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ContactRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ContactRepositoryImpl_Factory create(
      javax.inject.Provider<ContactDao> daoProvider) {
    return new ContactRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider));
  }

  public static ContactRepositoryImpl_Factory create(Provider<ContactDao> daoProvider) {
    return new ContactRepositoryImpl_Factory(daoProvider);
  }

  public static ContactRepositoryImpl newInstance(ContactDao dao) {
    return new ContactRepositoryImpl(dao);
  }
}
