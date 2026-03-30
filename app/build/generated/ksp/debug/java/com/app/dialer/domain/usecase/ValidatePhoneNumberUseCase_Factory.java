package com.app.dialer.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ValidatePhoneNumberUseCase_Factory implements Factory<ValidatePhoneNumberUseCase> {
  @Override
  public ValidatePhoneNumberUseCase get() {
    return newInstance();
  }

  public static ValidatePhoneNumberUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ValidatePhoneNumberUseCase newInstance() {
    return new ValidatePhoneNumberUseCase();
  }

  private static final class InstanceHolder {
    static final ValidatePhoneNumberUseCase_Factory INSTANCE = new ValidatePhoneNumberUseCase_Factory();
  }
}
