package com.v2ray.ang.payment.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.v2ray.ang.payment.data.local.entity.OrderEntity;
import java.lang.Class;
import java.lang.Long;
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
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<OrderEntity> __insertAdapterOfOrderEntity;

  private final EntityDeleteOrUpdateAdapter<OrderEntity> __deleteAdapterOfOrderEntity;

  private final EntityDeleteOrUpdateAdapter<OrderEntity> __updateAdapterOfOrderEntity;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfOrderEntity = new EntityInsertAdapter<OrderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `orders` (`orderNo`,`planName`,`planId`,`amount`,`assetCode`,`status`,`createdAt`,`paidAt`,`fulfilledAt`,`expiredAt`,`subscriptionUrl`,`marzbanUsername`,`userId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        if (entity.getOrderNo() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getOrderNo());
        }
        if (entity.getPlanName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getPlanName());
        }
        if (entity.getPlanId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getPlanId());
        }
        if (entity.getAmount() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAmount());
        }
        if (entity.getAssetCode() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getAssetCode());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getStatus());
        }
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getPaidAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getPaidAt());
        }
        if (entity.getFulfilledAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getFulfilledAt());
        }
        if (entity.getExpiredAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getExpiredAt());
        }
        if (entity.getSubscriptionUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getSubscriptionUrl());
        }
        if (entity.getMarzbanUsername() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getMarzbanUsername());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getUserId());
        }
      }
    };
    this.__deleteAdapterOfOrderEntity = new EntityDeleteOrUpdateAdapter<OrderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `orders` WHERE `orderNo` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        if (entity.getOrderNo() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getOrderNo());
        }
      }
    };
    this.__updateAdapterOfOrderEntity = new EntityDeleteOrUpdateAdapter<OrderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `orders` SET `orderNo` = ?,`planName` = ?,`planId` = ?,`amount` = ?,`assetCode` = ?,`status` = ?,`createdAt` = ?,`paidAt` = ?,`fulfilledAt` = ?,`expiredAt` = ?,`subscriptionUrl` = ?,`marzbanUsername` = ?,`userId` = ? WHERE `orderNo` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        if (entity.getOrderNo() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getOrderNo());
        }
        if (entity.getPlanName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getPlanName());
        }
        if (entity.getPlanId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getPlanId());
        }
        if (entity.getAmount() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAmount());
        }
        if (entity.getAssetCode() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getAssetCode());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getStatus());
        }
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getPaidAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getPaidAt());
        }
        if (entity.getFulfilledAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getFulfilledAt());
        }
        if (entity.getExpiredAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getExpiredAt());
        }
        if (entity.getSubscriptionUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getSubscriptionUrl());
        }
        if (entity.getMarzbanUsername() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getMarzbanUsername());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getUserId());
        }
        if (entity.getOrderNo() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getOrderNo());
        }
      }
    };
  }

  @Override
  public Object insert(final OrderEntity order, final Continuation<? super Unit> $completion) {
    if (order == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfOrderEntity.insert(_connection, order);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object delete(final OrderEntity order, final Continuation<? super Unit> $completion) {
    if (order == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfOrderEntity.handle(_connection, order);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final OrderEntity order, final Continuation<? super Unit> $completion) {
    if (order == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfOrderEntity.handle(_connection, order);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getByOrderNo(final String orderNo,
      final Continuation<? super OrderEntity> $completion) {
    final String _sql = "SELECT * FROM orders WHERE orderNo = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (orderNo == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, orderNo);
        }
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfPlanName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planName");
        final int _columnIndexOfPlanId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final int _columnIndexOfFulfilledAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fulfilledAt");
        final int _columnIndexOfExpiredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "expiredAt");
        final int _columnIndexOfSubscriptionUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subscriptionUrl");
        final int _columnIndexOfMarzbanUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marzbanUsername");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final OrderEntity _result;
        if (_stmt.step()) {
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpPlanName;
          if (_stmt.isNull(_columnIndexOfPlanName)) {
            _tmpPlanName = null;
          } else {
            _tmpPlanName = _stmt.getText(_columnIndexOfPlanName);
          }
          final String _tmpPlanId;
          if (_stmt.isNull(_columnIndexOfPlanId)) {
            _tmpPlanId = null;
          } else {
            _tmpPlanId = _stmt.getText(_columnIndexOfPlanId);
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
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final Long _tmpPaidAt;
          if (_stmt.isNull(_columnIndexOfPaidAt)) {
            _tmpPaidAt = null;
          } else {
            _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          }
          final Long _tmpFulfilledAt;
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null;
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt);
          }
          final Long _tmpExpiredAt;
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null;
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt);
          }
          final String _tmpSubscriptionUrl;
          if (_stmt.isNull(_columnIndexOfSubscriptionUrl)) {
            _tmpSubscriptionUrl = null;
          } else {
            _tmpSubscriptionUrl = _stmt.getText(_columnIndexOfSubscriptionUrl);
          }
          final String _tmpMarzbanUsername;
          if (_stmt.isNull(_columnIndexOfMarzbanUsername)) {
            _tmpMarzbanUsername = null;
          } else {
            _tmpMarzbanUsername = _stmt.getText(_columnIndexOfMarzbanUsername);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          _result = new OrderEntity(_tmpOrderNo,_tmpPlanName,_tmpPlanId,_tmpAmount,_tmpAssetCode,_tmpStatus,_tmpCreatedAt,_tmpPaidAt,_tmpFulfilledAt,_tmpExpiredAt,_tmpSubscriptionUrl,_tmpMarzbanUsername,_tmpUserId);
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
  public Object getAllByUserId(final String userId,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? ORDER BY createdAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfPlanName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planName");
        final int _columnIndexOfPlanId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final int _columnIndexOfFulfilledAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fulfilledAt");
        final int _columnIndexOfExpiredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "expiredAt");
        final int _columnIndexOfSubscriptionUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subscriptionUrl");
        final int _columnIndexOfMarzbanUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marzbanUsername");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final List<OrderEntity> _result = new ArrayList<OrderEntity>();
        while (_stmt.step()) {
          final OrderEntity _item;
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpPlanName;
          if (_stmt.isNull(_columnIndexOfPlanName)) {
            _tmpPlanName = null;
          } else {
            _tmpPlanName = _stmt.getText(_columnIndexOfPlanName);
          }
          final String _tmpPlanId;
          if (_stmt.isNull(_columnIndexOfPlanId)) {
            _tmpPlanId = null;
          } else {
            _tmpPlanId = _stmt.getText(_columnIndexOfPlanId);
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
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final Long _tmpPaidAt;
          if (_stmt.isNull(_columnIndexOfPaidAt)) {
            _tmpPaidAt = null;
          } else {
            _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          }
          final Long _tmpFulfilledAt;
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null;
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt);
          }
          final Long _tmpExpiredAt;
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null;
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt);
          }
          final String _tmpSubscriptionUrl;
          if (_stmt.isNull(_columnIndexOfSubscriptionUrl)) {
            _tmpSubscriptionUrl = null;
          } else {
            _tmpSubscriptionUrl = _stmt.getText(_columnIndexOfSubscriptionUrl);
          }
          final String _tmpMarzbanUsername;
          if (_stmt.isNull(_columnIndexOfMarzbanUsername)) {
            _tmpMarzbanUsername = null;
          } else {
            _tmpMarzbanUsername = _stmt.getText(_columnIndexOfMarzbanUsername);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          _item = new OrderEntity(_tmpOrderNo,_tmpPlanName,_tmpPlanId,_tmpAmount,_tmpAssetCode,_tmpStatus,_tmpCreatedAt,_tmpPaidAt,_tmpFulfilledAt,_tmpExpiredAt,_tmpSubscriptionUrl,_tmpMarzbanUsername,_tmpUserId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getByUserIdAndStatus(final String userId, final String status,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? AND status = ? ORDER BY createdAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        _argIndex = 2;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, status);
        }
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfPlanName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planName");
        final int _columnIndexOfPlanId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final int _columnIndexOfFulfilledAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fulfilledAt");
        final int _columnIndexOfExpiredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "expiredAt");
        final int _columnIndexOfSubscriptionUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subscriptionUrl");
        final int _columnIndexOfMarzbanUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marzbanUsername");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final List<OrderEntity> _result = new ArrayList<OrderEntity>();
        while (_stmt.step()) {
          final OrderEntity _item;
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpPlanName;
          if (_stmt.isNull(_columnIndexOfPlanName)) {
            _tmpPlanName = null;
          } else {
            _tmpPlanName = _stmt.getText(_columnIndexOfPlanName);
          }
          final String _tmpPlanId;
          if (_stmt.isNull(_columnIndexOfPlanId)) {
            _tmpPlanId = null;
          } else {
            _tmpPlanId = _stmt.getText(_columnIndexOfPlanId);
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
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final Long _tmpPaidAt;
          if (_stmt.isNull(_columnIndexOfPaidAt)) {
            _tmpPaidAt = null;
          } else {
            _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          }
          final Long _tmpFulfilledAt;
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null;
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt);
          }
          final Long _tmpExpiredAt;
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null;
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt);
          }
          final String _tmpSubscriptionUrl;
          if (_stmt.isNull(_columnIndexOfSubscriptionUrl)) {
            _tmpSubscriptionUrl = null;
          } else {
            _tmpSubscriptionUrl = _stmt.getText(_columnIndexOfSubscriptionUrl);
          }
          final String _tmpMarzbanUsername;
          if (_stmt.isNull(_columnIndexOfMarzbanUsername)) {
            _tmpMarzbanUsername = null;
          } else {
            _tmpMarzbanUsername = _stmt.getText(_columnIndexOfMarzbanUsername);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          _item = new OrderEntity(_tmpOrderNo,_tmpPlanName,_tmpPlanId,_tmpAmount,_tmpAssetCode,_tmpStatus,_tmpCreatedAt,_tmpPaidAt,_tmpFulfilledAt,_tmpExpiredAt,_tmpSubscriptionUrl,_tmpMarzbanUsername,_tmpUserId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getActiveOrders(final String userId, final long currentTime,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? AND expiredAt > ? ORDER BY expiredAt ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, currentTime);
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfPlanName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planName");
        final int _columnIndexOfPlanId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final int _columnIndexOfFulfilledAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fulfilledAt");
        final int _columnIndexOfExpiredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "expiredAt");
        final int _columnIndexOfSubscriptionUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subscriptionUrl");
        final int _columnIndexOfMarzbanUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marzbanUsername");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final List<OrderEntity> _result = new ArrayList<OrderEntity>();
        while (_stmt.step()) {
          final OrderEntity _item;
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpPlanName;
          if (_stmt.isNull(_columnIndexOfPlanName)) {
            _tmpPlanName = null;
          } else {
            _tmpPlanName = _stmt.getText(_columnIndexOfPlanName);
          }
          final String _tmpPlanId;
          if (_stmt.isNull(_columnIndexOfPlanId)) {
            _tmpPlanId = null;
          } else {
            _tmpPlanId = _stmt.getText(_columnIndexOfPlanId);
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
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final Long _tmpPaidAt;
          if (_stmt.isNull(_columnIndexOfPaidAt)) {
            _tmpPaidAt = null;
          } else {
            _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          }
          final Long _tmpFulfilledAt;
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null;
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt);
          }
          final Long _tmpExpiredAt;
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null;
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt);
          }
          final String _tmpSubscriptionUrl;
          if (_stmt.isNull(_columnIndexOfSubscriptionUrl)) {
            _tmpSubscriptionUrl = null;
          } else {
            _tmpSubscriptionUrl = _stmt.getText(_columnIndexOfSubscriptionUrl);
          }
          final String _tmpMarzbanUsername;
          if (_stmt.isNull(_columnIndexOfMarzbanUsername)) {
            _tmpMarzbanUsername = null;
          } else {
            _tmpMarzbanUsername = _stmt.getText(_columnIndexOfMarzbanUsername);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          _item = new OrderEntity(_tmpOrderNo,_tmpPlanName,_tmpPlanId,_tmpAmount,_tmpAssetCode,_tmpStatus,_tmpCreatedAt,_tmpPaidAt,_tmpFulfilledAt,_tmpExpiredAt,_tmpSubscriptionUrl,_tmpMarzbanUsername,_tmpUserId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getExpiringOrders(final String userId, final long currentTime, final long threshold,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? AND expiredAt > ? AND expiredAt <= ? ORDER BY expiredAt ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, currentTime);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, threshold);
        final int _columnIndexOfOrderNo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "orderNo");
        final int _columnIndexOfPlanName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planName");
        final int _columnIndexOfPlanId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "planId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfAssetCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "assetCode");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPaidAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paidAt");
        final int _columnIndexOfFulfilledAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fulfilledAt");
        final int _columnIndexOfExpiredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "expiredAt");
        final int _columnIndexOfSubscriptionUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subscriptionUrl");
        final int _columnIndexOfMarzbanUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marzbanUsername");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final List<OrderEntity> _result = new ArrayList<OrderEntity>();
        while (_stmt.step()) {
          final OrderEntity _item;
          final String _tmpOrderNo;
          if (_stmt.isNull(_columnIndexOfOrderNo)) {
            _tmpOrderNo = null;
          } else {
            _tmpOrderNo = _stmt.getText(_columnIndexOfOrderNo);
          }
          final String _tmpPlanName;
          if (_stmt.isNull(_columnIndexOfPlanName)) {
            _tmpPlanName = null;
          } else {
            _tmpPlanName = _stmt.getText(_columnIndexOfPlanName);
          }
          final String _tmpPlanId;
          if (_stmt.isNull(_columnIndexOfPlanId)) {
            _tmpPlanId = null;
          } else {
            _tmpPlanId = _stmt.getText(_columnIndexOfPlanId);
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
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final Long _tmpPaidAt;
          if (_stmt.isNull(_columnIndexOfPaidAt)) {
            _tmpPaidAt = null;
          } else {
            _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt);
          }
          final Long _tmpFulfilledAt;
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null;
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt);
          }
          final Long _tmpExpiredAt;
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null;
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt);
          }
          final String _tmpSubscriptionUrl;
          if (_stmt.isNull(_columnIndexOfSubscriptionUrl)) {
            _tmpSubscriptionUrl = null;
          } else {
            _tmpSubscriptionUrl = _stmt.getText(_columnIndexOfSubscriptionUrl);
          }
          final String _tmpMarzbanUsername;
          if (_stmt.isNull(_columnIndexOfMarzbanUsername)) {
            _tmpMarzbanUsername = null;
          } else {
            _tmpMarzbanUsername = _stmt.getText(_columnIndexOfMarzbanUsername);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          _item = new OrderEntity(_tmpOrderNo,_tmpPlanName,_tmpPlanId,_tmpAmount,_tmpAssetCode,_tmpStatus,_tmpCreatedAt,_tmpPaidAt,_tmpFulfilledAt,_tmpExpiredAt,_tmpSubscriptionUrl,_tmpMarzbanUsername,_tmpUserId);
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
    final String _sql = "DELETE FROM orders WHERE orderNo = ?";
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
  public Object deleteByUserId(final String userId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM orders WHERE userId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
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
