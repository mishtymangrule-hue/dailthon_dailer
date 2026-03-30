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
public final class SimCardHelper_Factory implements Factory<SimCardHelper> {
  private final Provider<Context> contextProvider;

  public SimCardHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SimCardHelper get() {
    return newInstance(contextProvider.get());
  }

  public static SimCardHelper_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new SimCardHelper_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static SimCardHelper_Factory create(Provider<Context> contextProvider) {
    return new SimCardHelper_Factory(contextProvider);
  }

  public static SimCardHelper newInstance(Context context) {
    return new SimCardHelper(context);
  }
}
