package com.v2ray.ang.payment.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.v2ray.ang.payment.data.local.dao.OrderDao;
import com.v2ray.ang.payment.data.local.dao.OrderDao_Impl;
import com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao;
import com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao_Impl;
import com.v2ray.ang.payment.data.local.dao.UserDao;
import com.v2ray.ang.payment.data.local.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class PaymentDatabase_Impl extends PaymentDatabase {
  private volatile UserDao _userDao;

  private volatile OrderDao _orderDao;

  private volatile PaymentHistoryDao _paymentHistoryDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "df9629d6d8e26475625e1f7e80a22a76", "835dbe8c0586edd5ccc9bd581be90f15") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `users` (`userId` TEXT NOT NULL, `username` TEXT NOT NULL, `email` TEXT, `accessToken` TEXT NOT NULL, `refreshToken` TEXT, `loginAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `orders` (`orderNo` TEXT NOT NULL, `planName` TEXT NOT NULL, `planId` TEXT NOT NULL, `amount` TEXT NOT NULL, `assetCode` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `paidAt` INTEGER, `fulfilledAt` INTEGER, `expiredAt` INTEGER, `subscriptionUrl` TEXT, `marzbanUsername` TEXT, `userId` TEXT NOT NULL, PRIMARY KEY(`orderNo`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_orders_orderNo` ON `orders` (`orderNo`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_orders_userId` ON `orders` (`userId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `payment_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `orderNo` TEXT NOT NULL, `amount` TEXT NOT NULL, `assetCode` TEXT NOT NULL, `txHash` TEXT, `paidAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_payment_history_orderNo` ON `payment_history` (`orderNo`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'df9629d6d8e26475625e1f7e80a22a76')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `users`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `orders`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `payment_history`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(6);
        _columnsUsers.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("accessToken", new TableInfo.Column("accessToken", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("refreshToken", new TableInfo.Column("refreshToken", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("loginAt", new TableInfo.Column("loginAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(1);
        _indicesUsers.add(new TableInfo.Index("index_users_username", true, Arrays.asList("username"), Arrays.asList("ASC")));
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(connection, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenDelegate.ValidationResult(false, "users(com.v2ray.ang.payment.data.local.entity.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final Map<String, TableInfo.Column> _columnsOrders = new HashMap<String, TableInfo.Column>(13);
        _columnsOrders.put("orderNo", new TableInfo.Column("orderNo", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("planName", new TableInfo.Column("planName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("planId", new TableInfo.Column("planId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("amount", new TableInfo.Column("amount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("assetCode", new TableInfo.Column("assetCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("paidAt", new TableInfo.Column("paidAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("fulfilledAt", new TableInfo.Column("fulfilledAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("expiredAt", new TableInfo.Column("expiredAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("subscriptionUrl", new TableInfo.Column("subscriptionUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("marzbanUsername", new TableInfo.Column("marzbanUsername", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysOrders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysOrders.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final Set<TableInfo.Index> _indicesOrders = new HashSet<TableInfo.Index>(2);
        _indicesOrders.add(new TableInfo.Index("index_orders_orderNo", true, Arrays.asList("orderNo"), Arrays.asList("ASC")));
        _indicesOrders.add(new TableInfo.Index("index_orders_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoOrders = new TableInfo("orders", _columnsOrders, _foreignKeysOrders, _indicesOrders);
        final TableInfo _existingOrders = TableInfo.read(connection, "orders");
        if (!_infoOrders.equals(_existingOrders)) {
          return new RoomOpenDelegate.ValidationResult(false, "orders(com.v2ray.ang.payment.data.local.entity.OrderEntity).\n"
                  + " Expected:\n" + _infoOrders + "\n"
                  + " Found:\n" + _existingOrders);
        }
        final Map<String, TableInfo.Column> _columnsPaymentHistory = new HashMap<String, TableInfo.Column>(6);
        _columnsPaymentHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentHistory.put("orderNo", new TableInfo.Column("orderNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentHistory.put("amount", new TableInfo.Column("amount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentHistory.put("assetCode", new TableInfo.Column("assetCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentHistory.put("txHash", new TableInfo.Column("txHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentHistory.put("paidAt", new TableInfo.Column("paidAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysPaymentHistory = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesPaymentHistory = new HashSet<TableInfo.Index>(1);
        _indicesPaymentHistory.add(new TableInfo.Index("index_payment_history_orderNo", false, Arrays.asList("orderNo"), Arrays.asList("ASC")));
        final TableInfo _infoPaymentHistory = new TableInfo("payment_history", _columnsPaymentHistory, _foreignKeysPaymentHistory, _indicesPaymentHistory);
        final TableInfo _existingPaymentHistory = TableInfo.read(connection, "payment_history");
        if (!_infoPaymentHistory.equals(_existingPaymentHistory)) {
          return new RoomOpenDelegate.ValidationResult(false, "payment_history(com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity).\n"
                  + " Expected:\n" + _infoPaymentHistory + "\n"
                  + " Found:\n" + _existingPaymentHistory);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "orders", "payment_history");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "users", "orders", "payment_history");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OrderDao.class, OrderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentHistoryDao.class, PaymentHistoryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
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
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public OrderDao orderDao() {
    if (_orderDao != null) {
      return _orderDao;
    } else {
      synchronized(this) {
        if(_orderDao == null) {
          _orderDao = new OrderDao_Impl(this);
        }
        return _orderDao;
      }
    }
  }

  @Override
  public PaymentHistoryDao paymentHistoryDao() {
    if (_paymentHistoryDao != null) {
      return _paymentHistoryDao;
    } else {
      synchronized(this) {
        if(_paymentHistoryDao == null) {
          _paymentHistoryDao = new PaymentHistoryDao_Impl(this);
        }
        return _paymentHistoryDao;
      }
    }
  }
}
