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
public final class FormatPhoneNumberUseCase_Factory implements Factory<FormatPhoneNumberUseCase> {
  @Override
  public FormatPhoneNumberUseCase get() {
    return newInstance();
  }

  public static FormatPhoneNumberUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FormatPhoneNumberUseCase newInstance() {
    return new FormatPhoneNumberUseCase();
  }

  private static final class InstanceHolder {
    static final FormatPhoneNumberUseCase_Factory INSTANCE = new FormatPhoneNumberUseCase_Factory();
  }
}
