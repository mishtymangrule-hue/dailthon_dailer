package com.app.dialer.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.app.dialer.core.telecom.TelecomManagerHelper;
import com.app.dialer.core.utils.SimCardHelper;
import com.app.dialer.data.local.RecentCallDao;
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
public final class DialerRepositoryImpl_Factory implements Factory<DialerRepositoryImpl> {
  private final Provider<TelecomManagerHelper> telecomManagerHelperProvider;

  private final Provider<RecentCallDao> recentCallDaoProvider;

  private final Provider<SimCardHelper> simCardHelperProvider;

  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public DialerRepositoryImpl_Factory(Provider<TelecomManagerHelper> telecomManagerHelperProvider,
      Provider<RecentCallDao> recentCallDaoProvider, Provider<SimCardHelper> simCardHelperProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.telecomManagerHelperProvider = telecomManagerHelperProvider;
    this.recentCallDaoProvider = recentCallDaoProvider;
    this.simCardHelperProvider = simCardHelperProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public DialerRepositoryImpl get() {
    return newInstance(telecomManagerHelperProvider.get(), recentCallDaoProvider.get(), simCardHelperProvider.get(), dataStoreProvider.get());
  }

  public static DialerRepositoryImpl_Factory create(
      javax.inject.Provider<TelecomManagerHelper> telecomManagerHelperProvider,
      javax.inject.Provider<RecentCallDao> recentCallDaoProvider,
      javax.inject.Provider<SimCardHelper> simCardHelperProvider,
      javax.inject.Provider<DataStore<Preferences>> dataStoreProvider) {
    return new DialerRepositoryImpl_Factory(Providers.asDaggerProvider(telecomManagerHelperProvider), Providers.asDaggerProvider(recentCallDaoProvider), Providers.asDaggerProvider(simCardHelperProvider), Providers.asDaggerProvider(dataStoreProvider));
  }

  public static DialerRepositoryImpl_Factory create(
      Provider<TelecomManagerHelper> telecomManagerHelperProvider,
      Provider<RecentCallDao> recentCallDaoProvider, Provider<SimCardHelper> simCardHelperProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new DialerRepositoryImpl_Factory(telecomManagerHelperProvider, recentCallDaoProvider, simCardHelperProvider, dataStoreProvider);
  }

  public static DialerRepositoryImpl newInstance(TelecomManagerHelper telecomManagerHelper,
      RecentCallDao recentCallDao, SimCardHelper simCardHelper, DataStore<Preferences> dataStore) {
    return new DialerRepositoryImpl(telecomManagerHelper, recentCallDao, simCardHelper, dataStore);
  }
}
