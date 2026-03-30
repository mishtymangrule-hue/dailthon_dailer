package com.app.dialer.presentation.dialer;

import android.content.Context;
import com.app.dialer.core.audio.AudioRouteManager;
import com.app.dialer.domain.usecase.FormatPhoneNumberUseCase;
import com.app.dialer.domain.usecase.GetAvailableSimCardsUseCase;
import com.app.dialer.domain.usecase.GetRecentCallsUseCase;
import com.app.dialer.domain.usecase.GetSuggestedContactsUseCase;
import com.app.dialer.domain.usecase.InitiateCallUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DialerViewModel_Factory implements Factory<DialerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<InitiateCallUseCase> initiateCallProvider;

  private final Provider<GetSuggestedContactsUseCase> getSuggestedContactsProvider;

  private final Provider<GetRecentCallsUseCase> getRecentCallsProvider;

  private final Provider<FormatPhoneNumberUseCase> formatPhoneNumberProvider;

  private final Provider<GetAvailableSimCardsUseCase> getAvailableSimCardsProvider;

  private final Provider<AudioRouteManager> audioRouteManagerProvider;

  public DialerViewModel_Factory(Provider<Context> contextProvider,
      Provider<InitiateCallUseCase> initiateCallProvider,
      Provider<GetSuggestedContactsUseCase> getSuggestedContactsProvider,
      Provider<GetRecentCallsUseCase> getRecentCallsProvider,
      Provider<FormatPhoneNumberUseCase> formatPhoneNumberProvider,
      Provider<GetAvailableSimCardsUseCase> getAvailableSimCardsProvider,
      Provider<AudioRouteManager> audioRouteManagerProvider) {
    this.contextProvider = contextProvider;
    this.initiateCallProvider = initiateCallProvider;
    this.getSuggestedContactsProvider = getSuggestedContactsProvider;
    this.getRecentCallsProvider = getRecentCallsProvider;
    this.formatPhoneNumberProvider = formatPhoneNumberProvider;
    this.getAvailableSimCardsProvider = getAvailableSimCardsProvider;
    this.audioRouteManagerProvider = audioRouteManagerProvider;
  }

  @Override
  public DialerViewModel get() {
    return newInstance(contextProvider.get(), initiateCallProvider.get(), getSuggestedContactsProvider.get(), getRecentCallsProvider.get(), formatPhoneNumberProvider.get(), getAvailableSimCardsProvider.get(), audioRouteManagerProvider.get());
  }

  public static DialerViewModel_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<InitiateCallUseCase> initiateCallProvider,
      javax.inject.Provider<GetSuggestedContactsUseCase> getSuggestedContactsProvider,
      javax.inject.Provider<GetRecentCallsUseCase> getRecentCallsProvider,
      javax.inject.Provider<FormatPhoneNumberUseCase> formatPhoneNumberProvider,
      javax.inject.Provider<GetAvailableSimCardsUseCase> getAvailableSimCardsProvider,
      javax.inject.Provider<AudioRouteManager> audioRouteManagerProvider) {
    return new DialerViewModel_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(initiateCallProvider), Providers.asDaggerProvider(getSuggestedContactsProvider), Providers.asDaggerProvider(getRecentCallsProvider), Providers.asDaggerProvider(formatPhoneNumberProvider), Providers.asDaggerProvider(getAvailableSimCardsProvider), Providers.asDaggerProvider(audioRouteManagerProvider));
  }

  public static DialerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<InitiateCallUseCase> initiateCallProvider,
      Provider<GetSuggestedContactsUseCase> getSuggestedContactsProvider,
      Provider<GetRecentCallsUseCase> getRecentCallsProvider,
      Provider<FormatPhoneNumberUseCase> formatPhoneNumberProvider,
      Provider<GetAvailableSimCardsUseCase> getAvailableSimCardsProvider,
      Provider<AudioRouteManager> audioRouteManagerProvider) {
    return new DialerViewModel_Factory(contextProvider, initiateCallProvider, getSuggestedContactsProvider, getRecentCallsProvider, formatPhoneNumberProvider, getAvailableSimCardsProvider, audioRouteManagerProvider);
  }

  public static DialerViewModel newInstance(Context context, InitiateCallUseCase initiateCall,
      GetSuggestedContactsUseCase getSuggestedContacts, GetRecentCallsUseCase getRecentCalls,
      FormatPhoneNumberUseCase formatPhoneNumber, GetAvailableSimCardsUseCase getAvailableSimCards,
      AudioRouteManager audioRouteManager) {
    return new DialerViewModel(context, initiateCall, getSuggestedContacts, getRecentCalls, formatPhoneNumber, getAvailableSimCards, audioRouteManager);
  }
}
