package com.app.dialer.core.telecom;

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
public final class CallEventBus_Factory implements Factory<CallEventBus> {
  @Override
  public CallEventBus get() {
    return newInstance();
  }

  public static CallEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CallEventBus newInstance() {
    return new CallEventBus();
  }

  private static final class InstanceHolder {
    static final CallEventBus_Factory INSTANCE = new CallEventBus_Factory();
  }
}
