package com.app.dialer.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.dialer.data.model.ContactEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ContactDao_Impl implements ContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ContactEntity> __insertionAdapterOfContactEntity;

  private final EntityDeletionOrUpdateAdapter<ContactEntity> __deletionAdapterOfContactEntity;

  private final EntityDeletionOrUpdateAdapter<ContactEntity> __updateAdapterOfContactEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ContactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContactEntity = new EntityInsertionAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `contacts` (`contact_id`,`display_name`,`phone_number`,`phone_type`,`photo_uri`,`is_starred`,`last_synced`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindLong(1, entity.getContactId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getPhoneNumber());
        statement.bindLong(4, entity.getPhoneType());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoUri());
        }
        final int _tmp = entity.isStarred() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getLastSynced());
      }
    };
    this.__deletionAdapterOfContactEntity = new EntityDeletionOrUpdateAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `contacts` WHERE `contact_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindLong(1, entity.getContactId());
      }
    };
    this.__updateAdapterOfContactEntity = new EntityDeletionOrUpdateAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `contacts` SET `contact_id` = ?,`display_name` = ?,`phone_number` = ?,`phone_type` = ?,`photo_uri` = ?,`is_starred` = ?,`last_synced` = ? WHERE `contact_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindLong(1, entity.getContactId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getPhoneNumber());
        statement.bindLong(4, entity.getPhoneType());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoUri());
        }
        final int _tmp = entity.isStarred() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getLastSynced());
        statement.bindLong(8, entity.getContactId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM contacts";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ContactEntity contact, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContactEntity.insert(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<ContactEntity> contacts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContactEntity.insert(contacts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ContactEntity contact, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfContactEntity.handle(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ContactEntity contact, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfContactEntity.handle(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ContactEntity>> observeAll() {
    final String _sql = "SELECT * FROM contacts ORDER BY display_name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "display_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfPhoneType = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_type");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_starred");
          final int _cursorIndexOfLastSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced");
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final int _tmpPhoneType;
            _tmpPhoneType = _cursor.getInt(_cursorIndexOfPhoneType);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            final long _tmpLastSynced;
            _tmpLastSynced = _cursor.getLong(_cursorIndexOfLastSynced);
            _item = new ContactEntity(_tmpContactId,_tmpDisplayName,_tmpPhoneNumber,_tmpPhoneType,_tmpPhotoUri,_tmpIsStarred,_tmpLastSynced);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ContactEntity>> observeSearch(final String query) {
    final String _sql = "SELECT * FROM contacts\n"
            + "           WHERE display_name LIKE '%' || ? || '%'\n"
            + "              OR phone_number LIKE '%' || ? || '%'\n"
            + "           ORDER BY display_name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "display_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfPhoneType = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_type");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_starred");
          final int _cursorIndexOfLastSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced");
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final int _tmpPhoneType;
            _tmpPhoneType = _cursor.getInt(_cursorIndexOfPhoneType);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            final long _tmpLastSynced;
            _tmpLastSynced = _cursor.getLong(_cursorIndexOfLastSynced);
            _item = new ContactEntity(_tmpContactId,_tmpDisplayName,_tmpPhoneNumber,_tmpPhoneType,_tmpPhotoUri,_tmpIsStarred,_tmpLastSynced);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ContactEntity>> observeFavorites() {
    final String _sql = "SELECT * FROM contacts WHERE is_starred = 1 ORDER BY display_name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "display_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfPhoneType = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_type");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_starred");
          final int _cursorIndexOfLastSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced");
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final int _tmpPhoneType;
            _tmpPhoneType = _cursor.getInt(_cursorIndexOfPhoneType);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            final long _tmpLastSynced;
            _tmpLastSynced = _cursor.getLong(_cursorIndexOfLastSynced);
            _item = new ContactEntity(_tmpContactId,_tmpDisplayName,_tmpPhoneNumber,_tmpPhoneType,_tmpPhotoUri,_tmpIsStarred,_tmpLastSynced);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super ContactEntity> $completion) {
    final String _sql = "SELECT * FROM contacts WHERE contact_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContactEntity>() {
      @Override
      @Nullable
      public ContactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "display_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfPhoneType = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_type");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_starred");
          final int _cursorIndexOfLastSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced");
          final ContactEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final int _tmpPhoneType;
            _tmpPhoneType = _cursor.getInt(_cursorIndexOfPhoneType);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            final long _tmpLastSynced;
            _tmpLastSynced = _cursor.getLong(_cursorIndexOfLastSynced);
            _result = new ContactEntity(_tmpContactId,_tmpDisplayName,_tmpPhoneNumber,_tmpPhoneType,_tmpPhotoUri,_tmpIsStarred,_tmpLastSynced);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByPhoneNumber(final String number,
      final Continuation<? super ContactEntity> $completion) {
    final String _sql = "SELECT * FROM contacts WHERE phone_number = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, number);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContactEntity>() {
      @Override
      @Nullable
      public ContactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "display_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfPhoneType = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_type");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "is_starred");
          final int _cursorIndexOfLastSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "last_synced");
          final ContactEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final int _tmpPhoneType;
            _tmpPhoneType = _cursor.getInt(_cursorIndexOfPhoneType);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            final long _tmpLastSynced;
            _tmpLastSynced = _cursor.getLong(_cursorIndexOfLastSynced);
            _result = new ContactEntity(_tmpContactId,_tmpDisplayName,_tmpPhoneNumber,_tmpPhoneType,_tmpPhotoUri,_tmpIsStarred,_tmpLastSynced);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
