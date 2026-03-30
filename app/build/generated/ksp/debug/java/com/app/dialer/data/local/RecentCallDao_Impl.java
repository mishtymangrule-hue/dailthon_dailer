package com.app.dialer.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.dialer.data.model.RecentCallEntity;
import com.app.dialer.domain.model.RecentCallType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class RecentCallDao_Impl implements RecentCallDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecentCallEntity> __insertionAdapterOfRecentCallEntity;

  private final CallTypeConverter __callTypeConverter = new CallTypeConverter();

  private final EntityDeletionOrUpdateAdapter<RecentCallEntity> __updateAdapterOfRecentCallEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsRead;

  public RecentCallDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecentCallEntity = new EntityInsertionAdapter<RecentCallEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recent_calls` (`id`,`contact_name`,`phone_number`,`call_type`,`duration_seconds`,`timestamp`,`photo_uri`,`is_read`,`sim_slot_index`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentCallEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getContactName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getContactName());
        }
        statement.bindString(3, entity.getPhoneNumber());
        final String _tmp = __callTypeConverter.fromRecentCallType(entity.getCallType());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getDurationSeconds());
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoUri());
        }
        final int _tmp_1 = entity.isRead() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getSimSlotIndex());
      }
    };
    this.__updateAdapterOfRecentCallEntity = new EntityDeletionOrUpdateAdapter<RecentCallEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recent_calls` SET `id` = ?,`contact_name` = ?,`phone_number` = ?,`call_type` = ?,`duration_seconds` = ?,`timestamp` = ?,`photo_uri` = ?,`is_read` = ?,`sim_slot_index` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentCallEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getContactName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getContactName());
        }
        statement.bindString(3, entity.getPhoneNumber());
        final String _tmp = __callTypeConverter.fromRecentCallType(entity.getCallType());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getDurationSeconds());
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoUri());
        }
        final int _tmp_1 = entity.isRead() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getSimSlotIndex());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recent_calls WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recent_calls SET is_read = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCallLog(final RecentCallEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecentCallEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCallLog(final RecentCallEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecentCallEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsRead(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsRead.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfMarkAsRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RecentCallEntity>> getRecentCalls(final int limit) {
    final String _sql = "SELECT * FROM recent_calls ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recent_calls"}, new Callable<List<RecentCallEntity>>() {
      @Override
      @NonNull
      public List<RecentCallEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phone_number");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "call_type");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_seconds");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "is_read");
          final int _cursorIndexOfSimSlotIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "sim_slot_index");
          final List<RecentCallEntity> _result = new ArrayList<RecentCallEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecentCallEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final RecentCallType _tmpCallType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCallType);
            _tmpCallType = __callTypeConverter.toRecentCallType(_tmp);
            final long _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getLong(_cursorIndexOfDurationSeconds);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final int _tmpSimSlotIndex;
            _tmpSimSlotIndex = _cursor.getInt(_cursorIndexOfSimSlotIndex);
            _item = new RecentCallEntity(_tmpId,_tmpContactName,_tmpPhoneNumber,_tmpCallType,_tmpDurationSeconds,_tmpTimestamp,_tmpPhotoUri,_tmpIsRead,_tmpSimSlotIndex);
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
  public Flow<Integer> getUnreadMissedCallCount() {
    final String _sql = "SELECT COUNT(*) FROM recent_calls WHERE call_type = 'MISSED' AND is_read = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recent_calls"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
