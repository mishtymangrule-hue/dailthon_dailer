package com.app.dialer.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DialerContactRepositoryImpl_Factory implements Factory<DialerContactRepositoryImpl> {
  @Override
  public DialerContactRepositoryImpl get() {
    return newInstance();
  }

  public static DialerContactRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DialerContactRepositoryImpl newInstance() {
    return new DialerContactRepositoryImpl();
  }

  private static final class InstanceHolder {
    static final DialerContactRepositoryImpl_Factory INSTANCE = new DialerContactRepositoryImpl_Factory();
  }
}
