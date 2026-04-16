package com.v2ray.ang.payment.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.v2ray.ang.payment.data.local.entity.UserEntity;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<UserEntity> __insertAdapterOfUserEntity;

  private final EntityDeleteOrUpdateAdapter<UserEntity> __deleteAdapterOfUserEntity;

  private final EntityDeleteOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUserEntity = new EntityInsertAdapter<UserEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `users` (`userId`,`username`,`email`,`accessToken`,`refreshToken`,`loginAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserEntity entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getUserId());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUsername());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getEmail());
        }
        if (entity.getAccessToken() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAccessToken());
        }
        if (entity.getRefreshToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getRefreshToken());
        }
        statement.bindLong(6, entity.getLoginAt());
      }
    };
    this.__deleteAdapterOfUserEntity = new EntityDeleteOrUpdateAdapter<UserEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `users` WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserEntity entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getUserId());
        }
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeleteOrUpdateAdapter<UserEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `userId` = ?,`username` = ?,`email` = ?,`accessToken` = ?,`refreshToken` = ?,`loginAt` = ? WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserEntity entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getUserId());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUsername());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getEmail());
        }
        if (entity.getAccessToken() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAccessToken());
        }
        if (entity.getRefreshToken() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getRefreshToken());
        }
        statement.bindLong(6, entity.getLoginAt());
        if (entity.getUserId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getUserId());
        }
      }
    };
  }

  @Override
  public Object insert(final UserEntity user, final Continuation<? super Long> $completion) {
    if (user == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserEntity.insertAndReturnId(_connection, user);
    }, $completion);
  }

  @Override
  public Object delete(final UserEntity user, final Continuation<? super Unit> $completion) {
    if (user == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfUserEntity.handle(_connection, user);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final UserEntity user, final Continuation<? super Unit> $completion) {
    if (user == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfUserEntity.handle(_connection, user);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getByUsername(final String username,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (username == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, username);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfAccessToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accessToken");
        final int _columnIndexOfRefreshToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "refreshToken");
        final int _columnIndexOfLoginAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "loginAt");
        final UserEntity _result;
        if (_stmt.step()) {
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final String _tmpUsername;
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _tmpUsername = null;
          } else {
            _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpAccessToken;
          if (_stmt.isNull(_columnIndexOfAccessToken)) {
            _tmpAccessToken = null;
          } else {
            _tmpAccessToken = _stmt.getText(_columnIndexOfAccessToken);
          }
          final String _tmpRefreshToken;
          if (_stmt.isNull(_columnIndexOfRefreshToken)) {
            _tmpRefreshToken = null;
          } else {
            _tmpRefreshToken = _stmt.getText(_columnIndexOfRefreshToken);
          }
          final long _tmpLoginAt;
          _tmpLoginAt = _stmt.getLong(_columnIndexOfLoginAt);
          _result = new UserEntity(_tmpUserId,_tmpUsername,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLoginAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getByUserId(final String userId,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE userId = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfAccessToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accessToken");
        final int _columnIndexOfRefreshToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "refreshToken");
        final int _columnIndexOfLoginAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "loginAt");
        final UserEntity _result;
        if (_stmt.step()) {
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final String _tmpUsername;
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _tmpUsername = null;
          } else {
            _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpAccessToken;
          if (_stmt.isNull(_columnIndexOfAccessToken)) {
            _tmpAccessToken = null;
          } else {
            _tmpAccessToken = _stmt.getText(_columnIndexOfAccessToken);
          }
          final String _tmpRefreshToken;
          if (_stmt.isNull(_columnIndexOfRefreshToken)) {
            _tmpRefreshToken = null;
          } else {
            _tmpRefreshToken = _stmt.getText(_columnIndexOfRefreshToken);
          }
          final long _tmpLoginAt;
          _tmpLoginAt = _stmt.getLong(_columnIndexOfLoginAt);
          _result = new UserEntity(_tmpUserId,_tmpUsername,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLoginAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getCurrentUser(final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfAccessToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accessToken");
        final int _columnIndexOfRefreshToken = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "refreshToken");
        final int _columnIndexOfLoginAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "loginAt");
        final UserEntity _result;
        if (_stmt.step()) {
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final String _tmpUsername;
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _tmpUsername = null;
          } else {
            _tmpUsername = _stmt.getText(_columnIndexOfUsername);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpAccessToken;
          if (_stmt.isNull(_columnIndexOfAccessToken)) {
            _tmpAccessToken = null;
          } else {
            _tmpAccessToken = _stmt.getText(_columnIndexOfAccessToken);
          }
          final String _tmpRefreshToken;
          if (_stmt.isNull(_columnIndexOfRefreshToken)) {
            _tmpRefreshToken = null;
          } else {
            _tmpRefreshToken = _stmt.getText(_columnIndexOfRefreshToken);
          }
          final long _tmpLoginAt;
          _tmpLoginAt = _stmt.getLong(_columnIndexOfLoginAt);
          _result = new UserEntity(_tmpUserId,_tmpUsername,_tmpEmail,_tmpAccessToken,_tmpRefreshToken,_tmpLoginAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM users";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
