package com.app.dialer.core.utils;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PhoneNumberFormatter_Factory implements Factory<PhoneNumberFormatter> {
  private final Provider<Context> contextProvider;

  public PhoneNumberFormatter_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PhoneNumberFormatter get() {
    return newInstance(contextProvider.get());
  }

  public static PhoneNumberFormatter_Factory create(
      javax.inject.Provider<Context> contextProvider) {
    return new PhoneNumberFormatter_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static PhoneNumberFormatter_Factory create(Provider<Context> contextProvider) {
    return new PhoneNumberFormatter_Factory(contextProvider);
  }

  public static PhoneNumberFormatter newInstance(Context context) {
    return new PhoneNumberFormatter(context);
  }
}
