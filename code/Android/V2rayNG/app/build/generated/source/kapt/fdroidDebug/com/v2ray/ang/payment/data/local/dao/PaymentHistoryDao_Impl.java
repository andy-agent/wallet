package com.v2ray.ang.payment.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity;
import java.lang.Class;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class PaymentHistoryDao_Impl implements PaymentHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<PaymentHistoryEntity> __insertAdapterOfPaymentHistoryEntity;

  private final EntityDeleteOrUpdateAdapter<PaymentHistoryEntity> __deleteAdapterOfPaymentHistoryEntity;

  public PaymentHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfPaymentHistoryEntity = new EntityInsertAdapter<PaymentHistoryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payment_history` (`id`,`orderNo`,`amount`,`assetCode`,`txHash`,`paidAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getOrderNo() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getOrderNo());
        }
        if (entity.getAmount() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getAmount());
        }
        if (entity.getAssetCode() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAssetCode());
        }
        if (entity.getTxHash() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getTxHash());
        }
        statement.bindLong(6, entity.getPaidAt());
      }
    };
    this.__deleteAdapterOfPaymentHistoryEntity = new EntityDeleteOrUpdateAdapter<PaymentHistoryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `payment_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final PaymentHistoryEntity paymentHistory,
      final Continuation<? super Unit> $completion) {
    if (paymentHistory == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfPaymentHistoryEntity.insert(_connection, paymentHistory);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object delete(final PaymentHistoryEntity paymentHistory,
      final Continuation<? super Unit> $completion) {
    if (paymentHistory == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfPaymentHistoryEntity.handle(_connection, paymentHistory);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getByOrderNo(final String orderNo,
      final Continuation<? super List<PaymentHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM payment_history WHERE orderNo = ? ORDER BY paidAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (orderNo == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, orderNo);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfTxHash = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "txHash");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final List<PaymentHistoryEntity> _result = new ArrayList<PaymentHistoryEntity>();
        while (_stmt.step()) {
          final PaymentHistoryEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpAmount;
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null;
          } else {
            _tmpAmount = _stmt.getText(_columnIndexOfAmount);
          }
          final String _tmpAssetCode;
          if (_stmt.isNull(_columnIndexOfAssetCode)) {
            _tmpAssetCode = null;
          } else {
            _tmpAssetCode = _stmt.getText(_columnIndexOfAssetCode);
          }
          final String _tmpTxHash;
          if (_stmt.isNull(_columnIndexOfTxHash)) {
            _tmpTxHash = null;
          } else {
            _tmpTxHash = _stmt.getText(_columnIndexOfTxHash);
          }
          final long _tmpPaidAt;
          _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          _item = new PaymentHistoryEntity(_tmpId,_tmpOrderNo,_tmpAmount,_tmpAssetCode,_tmpTxHash,_tmpPaidAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<PaymentHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM payment_history ORDER BY paidAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfTxHash = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "txHash");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final List<PaymentHistoryEntity> _result = new ArrayList<PaymentHistoryEntity>();
        while (_stmt.step()) {
          final PaymentHistoryEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpAmount;
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null;
          } else {
            _tmpAmount = _stmt.getText(_columnIndexOfAmount);
          }
          final String _tmpAssetCode;
          if (_stmt.isNull(_columnIndexOfAssetCode)) {
            _tmpAssetCode = null;
          } else {
            _tmpAssetCode = _stmt.getText(_columnIndexOfAssetCode);
          }
          final String _tmpTxHash;
          if (_stmt.isNull(_columnIndexOfTxHash)) {
            _tmpTxHash = null;
          } else {
            _tmpTxHash = _stmt.getText(_columnIndexOfTxHash);
          }
          final long _tmpPaidAt;
          _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          _item = new PaymentHistoryEntity(_tmpId,_tmpOrderNo,_tmpAmount,_tmpAssetCode,_tmpTxHash,_tmpPaidAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllByUserId(final String userId,
      final Continuation<? super List<PaymentHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM payment_history WHERE orderNo IN (SELECT orderNo FROM orders WHERE userId = ?) ORDER BY paidAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfTxHash = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "txHash");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final List<PaymentHistoryEntity> _result = new ArrayList<PaymentHistoryEntity>();
        while (_stmt.step()) {
          final PaymentHistoryEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpAmount;
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null;
          } else {
            _tmpAmount = _stmt.getText(_columnIndexOfAmount);
          }
          final String _tmpAssetCode;
          if (_stmt.isNull(_columnIndexOfAssetCode)) {
            _tmpAssetCode = null;
          } else {
            _tmpAssetCode = _stmt.getText(_columnIndexOfAssetCode);
          }
          final String _tmpTxHash;
          if (_stmt.isNull(_columnIndexOfTxHash)) {
            _tmpTxHash = null;
          } else {
            _tmpTxHash = _stmt.getText(_columnIndexOfTxHash);
          }
          final long _tmpPaidAt;
          _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          _item = new PaymentHistoryEntity(_tmpId,_tmpOrderNo,_tmpAmount,_tmpAssetCode,_tmpTxHash,_tmpPaidAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteByOrderNo(final String orderNo,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM payment_history WHERE orderNo = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (orderNo == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, orderNo);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM payment_history";
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
