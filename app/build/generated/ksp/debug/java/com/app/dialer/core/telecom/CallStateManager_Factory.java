package com.app.dialer.core.telecom;

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
public final class CallStateManager_Factory implements Factory<CallStateManager> {
  private final Provider<CallEventBus> eventBusProvider;

  public CallStateManager_Factory(Provider<CallEventBus> eventBusProvider) {
    this.eventBusProvider = eventBusProvider;
  }

  @Override
  public CallStateManager get() {
    return newInstance(eventBusProvider.get());
  }

  public static CallStateManager_Factory create(
      javax.inject.Provider<CallEventBus> eventBusProvider) {
    return new CallStateManager_Factory(Providers.asDaggerProvider(eventBusProvider));
  }

  public static CallStateManager_Factory create(Provider<CallEventBus> eventBusProvider) {
    return new CallStateManager_Factory(eventBusProvider);
  }

  public static CallStateManager newInstance(CallEventBus eventBus) {
    return new CallStateManager(eventBus);
  }
}
