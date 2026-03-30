package com.app.dialer.core.telecom;

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
public final class TelecomManagerHelper_Factory implements Factory<TelecomManagerHelper> {
  private final Provider<Context> contextProvider;

  public TelecomManagerHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TelecomManagerHelper get() {
    return newInstance(contextProvider.get());
  }

  public static TelecomManagerHelper_Factory create(
      javax.inject.Provider<Context> contextProvider) {
    return new TelecomManagerHelper_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static TelecomManagerHelper_Factory create(Provider<Context> contextProvider) {
    return new TelecomManagerHelper_Factory(contextProvider);
  }

  public static TelecomManagerHelper newInstance(Context context) {
    return new TelecomManagerHelper(context);
  }
}
