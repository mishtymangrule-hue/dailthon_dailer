package com.app.dialer.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RecentCallDao _recentCallDao;

  private volatile CallLogDao _callLogDao;

  private volatile ContactDao _contactDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent_calls` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `contact_name` TEXT, `phone_number` TEXT NOT NULL, `call_type` TEXT NOT NULL, `duration_seconds` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `photo_uri` TEXT, `is_read` INTEGER NOT NULL, `sim_slot_index` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `call_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phone_number` TEXT NOT NULL, `contact_name` TEXT, `call_type` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `duration_seconds` INTEGER NOT NULL, `is_read` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `contacts` (`contact_id` INTEGER NOT NULL, `display_name` TEXT NOT NULL, `phone_number` TEXT NOT NULL, `phone_type` INTEGER NOT NULL, `photo_uri` TEXT, `is_starred` INTEGER NOT NULL, `last_synced` INTEGER NOT NULL, PRIMARY KEY(`contact_id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7025e858e3067b109513b0a498a3b0f5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `recent_calls`");
        db.execSQL("DROP TABLE IF EXISTS `call_logs`");
        db.execSQL("DROP TABLE IF EXISTS `contacts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRecentCalls = new HashMap<String, TableInfo.Column>(9);
        _columnsRecentCalls.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("contact_name", new TableInfo.Column("contact_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("phone_number", new TableInfo.Column("phone_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("call_type", new TableInfo.Column("call_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("duration_seconds", new TableInfo.Column("duration_seconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("photo_uri", new TableInfo.Column("photo_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("is_read", new TableInfo.Column("is_read", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentCalls.put("sim_slot_index", new TableInfo.Column("sim_slot_index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentCalls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecentCalls = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecentCalls = new TableInfo("recent_calls", _columnsRecentCalls, _foreignKeysRecentCalls, _indicesRecentCalls);
        final TableInfo _existingRecentCalls = TableInfo.read(db, "recent_calls");
        if (!_infoRecentCalls.equals(_existingRecentCalls)) {
          return new RoomOpenHelper.ValidationResult(false, "recent_calls(com.app.dialer.data.model.RecentCallEntity).\n"
                  + " Expected:\n" + _infoRecentCalls + "\n"
                  + " Found:\n" + _existingRecentCalls);
        }
        final HashMap<String, TableInfo.Column> _columnsCallLogs = new HashMap<String, TableInfo.Column>(7);
        _columnsCallLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("phone_number", new TableInfo.Column("phone_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("contact_name", new TableInfo.Column("contact_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("call_type", new TableInfo.Column("call_type", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("duration_seconds", new TableInfo.Column("duration_seconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCallLogs.put("is_read", new TableInfo.Column("is_read", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCallLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCallLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCallLogs = new TableInfo("call_logs", _columnsCallLogs, _foreignKeysCallLogs, _indicesCallLogs);
        final TableInfo _existingCallLogs = TableInfo.read(db, "call_logs");
        if (!_infoCallLogs.equals(_existingCallLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "call_logs(com.app.dialer.data.model.CallLogEntity).\n"
                  + " Expected:\n" + _infoCallLogs + "\n"
                  + " Found:\n" + _existingCallLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsContacts = new HashMap<String, TableInfo.Column>(7);
        _columnsContacts.put("contact_id", new TableInfo.Column("contact_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("display_name", new TableInfo.Column("display_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("phone_number", new TableInfo.Column("phone_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("phone_type", new TableInfo.Column("phone_type", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("photo_uri", new TableInfo.Column("photo_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("is_starred", new TableInfo.Column("is_starred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("last_synced", new TableInfo.Column("last_synced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContacts = new TableInfo("contacts", _columnsContacts, _foreignKeysContacts, _indicesContacts);
        final TableInfo _existingContacts = TableInfo.read(db, "contacts");
        if (!_infoContacts.equals(_existingContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "contacts(com.app.dialer.data.model.ContactEntity).\n"
                  + " Expected:\n" + _infoContacts + "\n"
                  + " Found:\n" + _existingContacts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7025e858e3067b109513b0a498a3b0f5", "d714c99585359fcb33dbb3d541bcc7e1");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "recent_calls","call_logs","contacts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `recent_calls`");
      _db.execSQL("DELETE FROM `call_logs`");
      _db.execSQL("DELETE FROM `contacts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RecentCallDao.class, RecentCallDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CallLogDao.class, CallLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RecentCallDao recentCallDao() {
    if (_recentCallDao != null) {
      return _recentCallDao;
    } else {
      synchronized(this) {
        if(_recentCallDao == null) {
          _recentCallDao = new RecentCallDao_Impl(this);
        }
        return _recentCallDao;
      }
    }
  }

  @Override
  public CallLogDao callLogDao() {
    if (_callLogDao != null) {
      return _callLogDao;
    } else {
      synchronized(this) {
        if(_callLogDao == null) {
          _callLogDao = new CallLogDao_Impl(this);
        }
        return _callLogDao;
      }
    }
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }
}
