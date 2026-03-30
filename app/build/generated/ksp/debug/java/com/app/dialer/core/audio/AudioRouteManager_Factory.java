package com.app.dialer.core.audio;

import android.content.Context;
import com.app.dialer.core.telecom.CallEventBus;
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
public final class AudioRouteManager_Factory implements Factory<AudioRouteManager> {
  private final Provider<Context> contextProvider;

  private final Provider<CallEventBus> eventBusProvider;

  public AudioRouteManager_Factory(Provider<Context> contextProvider,
      Provider<CallEventBus> eventBusProvider) {
    this.contextProvider = contextProvider;
    this.eventBusProvider = eventBusProvider;
  }

  @Override
  public AudioRouteManager get() {
    return newInstance(contextProvider.get(), eventBusProvider.get());
  }

  public static AudioRouteManager_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<CallEventBus> eventBusProvider) {
    return new AudioRouteManager_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(eventBusProvider));
  }

  public static AudioRouteManager_Factory create(Provider<Context> contextProvider,
      Provider<CallEventBus> eventBusProvider) {
    return new AudioRouteManager_Factory(contextProvider, eventBusProvider);
  }

  public static AudioRouteManager newInstance(Context context, CallEventBus eventBus) {
    return new AudioRouteManager(context, eventBus);
  }
}
