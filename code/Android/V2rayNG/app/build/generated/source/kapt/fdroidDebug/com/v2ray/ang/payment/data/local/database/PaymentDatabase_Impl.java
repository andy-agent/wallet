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
import com.v2ray.ang.payment.data.local.dao.VpnNodeCacheDao;
import com.v2ray.ang.payment.data.local.dao.VpnNodeCacheDao_Impl;
import com.v2ray.ang.payment.data.local.dao.VpnNodeRuntimeDao;
import com.v2ray.ang.payment.data.local.dao.VpnNodeRuntimeDao_Impl;
import com.v2ray.ang.payment.data.local.dao.WalletOverviewCacheDao;
import com.v2ray.ang.payment.data.local.dao.WalletOverviewCacheDao_Impl;
import com.v2ray.ang.payment.data.local.dao.WalletPublicAddressCacheDao;
import com.v2ray.ang.payment.data.local.dao.WalletPublicAddressCacheDao_Impl;
import com.v2ray.ang.payment.data.local.dao.WalletReceiveContextCacheDao;
import com.v2ray.ang.payment.data.local.dao.WalletReceiveContextCacheDao_Impl;
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

  private volatile VpnNodeCacheDao _vpnNodeCacheDao;

  private volatile VpnNodeRuntimeDao _vpnNodeRuntimeDao;

  private volatile WalletPublicAddressCacheDao _walletPublicAddressCacheDao;

  private volatile WalletReceiveContextCacheDao _walletReceiveContextCacheDao;

  private volatile WalletOverviewCacheDao _walletOverviewCacheDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(5, "12b31ed802b8ae700e3d76bcd1e1227b", "6dfd91d6ac34f18f78a93f99333e4f81") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `users` (`userId` TEXT NOT NULL, `username` TEXT NOT NULL, `email` TEXT, `accessToken` TEXT NOT NULL, `refreshToken` TEXT, `loginAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `orders` (`orderNo` TEXT NOT NULL, `planName` TEXT NOT NULL, `planId` TEXT NOT NULL, `amount` TEXT NOT NULL, `usdAmount` TEXT NOT NULL, `assetCode` TEXT NOT NULL, `networkCode` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `paidAt` INTEGER, `fulfilledAt` INTEGER, `expiredAt` INTEGER, `subscriptionUrl` TEXT, `marzbanUsername` TEXT, `userId` TEXT NOT NULL, PRIMARY KEY(`orderNo`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_orders_orderNo` ON `orders` (`orderNo`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_orders_userId` ON `orders` (`userId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `payment_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `orderNo` TEXT NOT NULL, `amount` TEXT NOT NULL, `assetCode` TEXT NOT NULL, `txHash` TEXT, `paidAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_payment_history_orderNo` ON `payment_history` (`orderNo`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `vpn_node_cache` (`userId` TEXT NOT NULL, `nodeId` TEXT NOT NULL, `nodeName` TEXT NOT NULL, `lineCode` TEXT NOT NULL, `lineName` TEXT NOT NULL, `regionCode` TEXT NOT NULL, `regionName` TEXT NOT NULL, `host` TEXT NOT NULL, `port` INTEGER NOT NULL, `status` TEXT NOT NULL, `source` TEXT NOT NULL, `remark` TEXT, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `nodeId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_userId` ON `vpn_node_cache` (`userId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_lineCode` ON `vpn_node_cache` (`lineCode`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_regionCode` ON `vpn_node_cache` (`regionCode`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `vpn_node_runtime` (`userId` TEXT NOT NULL, `nodeId` TEXT NOT NULL, `lineCode` TEXT NOT NULL, `healthStatus` TEXT NOT NULL, `pingMs` INTEGER, `selected` INTEGER NOT NULL, `lastSeenAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `nodeId`))");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_vpn_node_runtime_userId` ON `vpn_node_runtime` (`userId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_vpn_node_runtime_lineCode` ON `vpn_node_runtime` (`lineCode`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `wallet_public_address_cache` (`userId` TEXT NOT NULL, `addressId` TEXT NOT NULL, `accountId` TEXT NOT NULL, `networkCode` TEXT NOT NULL, `assetCode` TEXT NOT NULL, `address` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `addressId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_userId` ON `wallet_public_address_cache` (`userId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_networkCode` ON `wallet_public_address_cache` (`networkCode`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_assetCode` ON `wallet_public_address_cache` (`assetCode`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `wallet_receive_context_cache` (`userId` TEXT NOT NULL, `requestNetworkCode` TEXT NOT NULL, `requestAssetCode` TEXT NOT NULL, `selectedNetworkCode` TEXT NOT NULL, `selectedAssetCode` TEXT NOT NULL, `chainItemsJson` TEXT NOT NULL, `assetItemsJson` TEXT NOT NULL, `defaultAddress` TEXT, `canShare` INTEGER NOT NULL, `walletExists` INTEGER NOT NULL, `receiveState` TEXT, `status` TEXT NOT NULL, `note` TEXT NOT NULL, `shareText` TEXT, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `requestNetworkCode`, `requestAssetCode`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_userId` ON `wallet_receive_context_cache` (`userId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_selectedNetworkCode` ON `wallet_receive_context_cache` (`selectedNetworkCode`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_selectedAssetCode` ON `wallet_receive_context_cache` (`selectedAssetCode`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `wallet_overview_cache` (`userId` TEXT NOT NULL, `accountId` TEXT NOT NULL, `accountEmail` TEXT NOT NULL, `selectedNetworkCode` TEXT NOT NULL, `chainItemsJson` TEXT NOT NULL, `assetItemsJson` TEXT NOT NULL, `alertsJson` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_wallet_overview_cache_userId` ON `wallet_overview_cache` (`userId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '12b31ed802b8ae700e3d76bcd1e1227b')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `users`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `orders`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `payment_history`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `vpn_node_cache`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `vpn_node_runtime`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `wallet_public_address_cache`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `wallet_receive_context_cache`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `wallet_overview_cache`");
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
        final Map<String, TableInfo.Column> _columnsOrders = new HashMap<String, TableInfo.Column>(15);
        _columnsOrders.put("orderNo", new TableInfo.Column("orderNo", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("planName", new TableInfo.Column("planName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("planId", new TableInfo.Column("planId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("amount", new TableInfo.Column("amount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("usdAmount", new TableInfo.Column("usdAmount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("assetCode", new TableInfo.Column("assetCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("networkCode", new TableInfo.Column("networkCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
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
        final Map<String, TableInfo.Column> _columnsVpnNodeCache = new HashMap<String, TableInfo.Column>(13);
        _columnsVpnNodeCache.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("nodeId", new TableInfo.Column("nodeId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("nodeName", new TableInfo.Column("nodeName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("lineCode", new TableInfo.Column("lineCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("lineName", new TableInfo.Column("lineName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("regionCode", new TableInfo.Column("regionCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("regionName", new TableInfo.Column("regionName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("host", new TableInfo.Column("host", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("port", new TableInfo.Column("port", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("remark", new TableInfo.Column("remark", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeCache.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysVpnNodeCache = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVpnNodeCache.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final Set<TableInfo.Index> _indicesVpnNodeCache = new HashSet<TableInfo.Index>(3);
        _indicesVpnNodeCache.add(new TableInfo.Index("index_vpn_node_cache_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesVpnNodeCache.add(new TableInfo.Index("index_vpn_node_cache_lineCode", false, Arrays.asList("lineCode"), Arrays.asList("ASC")));
        _indicesVpnNodeCache.add(new TableInfo.Index("index_vpn_node_cache_regionCode", false, Arrays.asList("regionCode"), Arrays.asList("ASC")));
        final TableInfo _infoVpnNodeCache = new TableInfo("vpn_node_cache", _columnsVpnNodeCache, _foreignKeysVpnNodeCache, _indicesVpnNodeCache);
        final TableInfo _existingVpnNodeCache = TableInfo.read(connection, "vpn_node_cache");
        if (!_infoVpnNodeCache.equals(_existingVpnNodeCache)) {
          return new RoomOpenDelegate.ValidationResult(false, "vpn_node_cache(com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity).\n"
                  + " Expected:\n" + _infoVpnNodeCache + "\n"
                  + " Found:\n" + _existingVpnNodeCache);
        }
        final Map<String, TableInfo.Column> _columnsVpnNodeRuntime = new HashMap<String, TableInfo.Column>(7);
        _columnsVpnNodeRuntime.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("nodeId", new TableInfo.Column("nodeId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("lineCode", new TableInfo.Column("lineCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("healthStatus", new TableInfo.Column("healthStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("pingMs", new TableInfo.Column("pingMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("selected", new TableInfo.Column("selected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVpnNodeRuntime.put("lastSeenAt", new TableInfo.Column("lastSeenAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysVpnNodeRuntime = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesVpnNodeRuntime = new HashSet<TableInfo.Index>(2);
        _indicesVpnNodeRuntime.add(new TableInfo.Index("index_vpn_node_runtime_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesVpnNodeRuntime.add(new TableInfo.Index("index_vpn_node_runtime_lineCode", false, Arrays.asList("lineCode"), Arrays.asList("ASC")));
        final TableInfo _infoVpnNodeRuntime = new TableInfo("vpn_node_runtime", _columnsVpnNodeRuntime, _foreignKeysVpnNodeRuntime, _indicesVpnNodeRuntime);
        final TableInfo _existingVpnNodeRuntime = TableInfo.read(connection, "vpn_node_runtime");
        if (!_infoVpnNodeRuntime.equals(_existingVpnNodeRuntime)) {
          return new RoomOpenDelegate.ValidationResult(false, "vpn_node_runtime(com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity).\n"
                  + " Expected:\n" + _infoVpnNodeRuntime + "\n"
                  + " Found:\n" + _existingVpnNodeRuntime);
        }
        final Map<String, TableInfo.Column> _columnsWalletPublicAddressCache = new HashMap<String, TableInfo.Column>(9);
        _columnsWalletPublicAddressCache.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("addressId", new TableInfo.Column("addressId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("accountId", new TableInfo.Column("accountId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("networkCode", new TableInfo.Column("networkCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("assetCode", new TableInfo.Column("assetCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletPublicAddressCache.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWalletPublicAddressCache = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWalletPublicAddressCache.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final Set<TableInfo.Index> _indicesWalletPublicAddressCache = new HashSet<TableInfo.Index>(3);
        _indicesWalletPublicAddressCache.add(new TableInfo.Index("index_wallet_public_address_cache_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesWalletPublicAddressCache.add(new TableInfo.Index("index_wallet_public_address_cache_networkCode", false, Arrays.asList("networkCode"), Arrays.asList("ASC")));
        _indicesWalletPublicAddressCache.add(new TableInfo.Index("index_wallet_public_address_cache_assetCode", false, Arrays.asList("assetCode"), Arrays.asList("ASC")));
        final TableInfo _infoWalletPublicAddressCache = new TableInfo("wallet_public_address_cache", _columnsWalletPublicAddressCache, _foreignKeysWalletPublicAddressCache, _indicesWalletPublicAddressCache);
        final TableInfo _existingWalletPublicAddressCache = TableInfo.read(connection, "wallet_public_address_cache");
        if (!_infoWalletPublicAddressCache.equals(_existingWalletPublicAddressCache)) {
          return new RoomOpenDelegate.ValidationResult(false, "wallet_public_address_cache(com.v2ray.ang.payment.data.local.entity.WalletPublicAddressCacheEntity).\n"
                  + " Expected:\n" + _infoWalletPublicAddressCache + "\n"
                  + " Found:\n" + _existingWalletPublicAddressCache);
        }
        final Map<String, TableInfo.Column> _columnsWalletReceiveContextCache = new HashMap<String, TableInfo.Column>(15);
        _columnsWalletReceiveContextCache.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("requestNetworkCode", new TableInfo.Column("requestNetworkCode", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("requestAssetCode", new TableInfo.Column("requestAssetCode", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("selectedNetworkCode", new TableInfo.Column("selectedNetworkCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("selectedAssetCode", new TableInfo.Column("selectedAssetCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("chainItemsJson", new TableInfo.Column("chainItemsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("assetItemsJson", new TableInfo.Column("assetItemsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("defaultAddress", new TableInfo.Column("defaultAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("canShare", new TableInfo.Column("canShare", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("walletExists", new TableInfo.Column("walletExists", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("receiveState", new TableInfo.Column("receiveState", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("shareText", new TableInfo.Column("shareText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletReceiveContextCache.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWalletReceiveContextCache = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWalletReceiveContextCache.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final Set<TableInfo.Index> _indicesWalletReceiveContextCache = new HashSet<TableInfo.Index>(3);
        _indicesWalletReceiveContextCache.add(new TableInfo.Index("index_wallet_receive_context_cache_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesWalletReceiveContextCache.add(new TableInfo.Index("index_wallet_receive_context_cache_selectedNetworkCode", false, Arrays.asList("selectedNetworkCode"), Arrays.asList("ASC")));
        _indicesWalletReceiveContextCache.add(new TableInfo.Index("index_wallet_receive_context_cache_selectedAssetCode", false, Arrays.asList("selectedAssetCode"), Arrays.asList("ASC")));
        final TableInfo _infoWalletReceiveContextCache = new TableInfo("wallet_receive_context_cache", _columnsWalletReceiveContextCache, _foreignKeysWalletReceiveContextCache, _indicesWalletReceiveContextCache);
        final TableInfo _existingWalletReceiveContextCache = TableInfo.read(connection, "wallet_receive_context_cache");
        if (!_infoWalletReceiveContextCache.equals(_existingWalletReceiveContextCache)) {
          return new RoomOpenDelegate.ValidationResult(false, "wallet_receive_context_cache(com.v2ray.ang.payment.data.local.entity.WalletReceiveContextCacheEntity).\n"
                  + " Expected:\n" + _infoWalletReceiveContextCache + "\n"
                  + " Found:\n" + _existingWalletReceiveContextCache);
        }
        final Map<String, TableInfo.Column> _columnsWalletOverviewCache = new HashMap<String, TableInfo.Column>(8);
        _columnsWalletOverviewCache.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("accountId", new TableInfo.Column("accountId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("accountEmail", new TableInfo.Column("accountEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("selectedNetworkCode", new TableInfo.Column("selectedNetworkCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("chainItemsJson", new TableInfo.Column("chainItemsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("assetItemsJson", new TableInfo.Column("assetItemsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("alertsJson", new TableInfo.Column("alertsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWalletOverviewCache.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWalletOverviewCache = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWalletOverviewCache.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final Set<TableInfo.Index> _indicesWalletOverviewCache = new HashSet<TableInfo.Index>(1);
        _indicesWalletOverviewCache.add(new TableInfo.Index("index_wallet_overview_cache_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoWalletOverviewCache = new TableInfo("wallet_overview_cache", _columnsWalletOverviewCache, _foreignKeysWalletOverviewCache, _indicesWalletOverviewCache);
        final TableInfo _existingWalletOverviewCache = TableInfo.read(connection, "wallet_overview_cache");
        if (!_infoWalletOverviewCache.equals(_existingWalletOverviewCache)) {
          return new RoomOpenDelegate.ValidationResult(false, "wallet_overview_cache(com.v2ray.ang.payment.data.local.entity.WalletOverviewCacheEntity).\n"
                  + " Expected:\n" + _infoWalletOverviewCache + "\n"
                  + " Found:\n" + _existingWalletOverviewCache);
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
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "orders", "payment_history", "vpn_node_cache", "vpn_node_runtime", "wallet_public_address_cache", "wallet_receive_context_cache", "wallet_overview_cache");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "users", "orders", "payment_history", "vpn_node_cache", "vpn_node_runtime", "wallet_public_address_cache", "wallet_receive_context_cache", "wallet_overview_cache");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OrderDao.class, OrderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentHistoryDao.class, PaymentHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VpnNodeCacheDao.class, VpnNodeCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VpnNodeRuntimeDao.class, VpnNodeRuntimeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WalletPublicAddressCacheDao.class, WalletPublicAddressCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WalletReceiveContextCacheDao.class, WalletReceiveContextCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WalletOverviewCacheDao.class, WalletOverviewCacheDao_Impl.getRequiredConverters());
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

  @Override
  public VpnNodeCacheDao vpnNodeCacheDao() {
    if (_vpnNodeCacheDao != null) {
      return _vpnNodeCacheDao;
    } else {
      synchronized(this) {
        if(_vpnNodeCacheDao == null) {
          _vpnNodeCacheDao = new VpnNodeCacheDao_Impl(this);
        }
        return _vpnNodeCacheDao;
      }
    }
  }

  @Override
  public VpnNodeRuntimeDao vpnNodeRuntimeDao() {
    if (_vpnNodeRuntimeDao != null) {
      return _vpnNodeRuntimeDao;
    } else {
      synchronized(this) {
        if(_vpnNodeRuntimeDao == null) {
          _vpnNodeRuntimeDao = new VpnNodeRuntimeDao_Impl(this);
        }
        return _vpnNodeRuntimeDao;
      }
    }
  }

  @Override
  public WalletPublicAddressCacheDao walletPublicAddressCacheDao() {
    if (_walletPublicAddressCacheDao != null) {
      return _walletPublicAddressCacheDao;
    } else {
      synchronized(this) {
        if(_walletPublicAddressCacheDao == null) {
          _walletPublicAddressCacheDao = new WalletPublicAddressCacheDao_Impl(this);
        }
        return _walletPublicAddressCacheDao;
      }
    }
  }

  @Override
  public WalletReceiveContextCacheDao walletReceiveContextCacheDao() {
    if (_walletReceiveContextCacheDao != null) {
      return _walletReceiveContextCacheDao;
    } else {
      synchronized(this) {
        if(_walletReceiveContextCacheDao == null) {
          _walletReceiveContextCacheDao = new WalletReceiveContextCacheDao_Impl(this);
        }
        return _walletReceiveContextCacheDao;
      }
    }
  }

  @Override
  public WalletOverviewCacheDao walletOverviewCacheDao() {
    if (_walletOverviewCacheDao != null) {
      return _walletOverviewCacheDao;
    } else {
      synchronized(this) {
        if(_walletOverviewCacheDao == null) {
          _walletOverviewCacheDao = new WalletOverviewCacheDao_Impl(this);
        }
        return _walletOverviewCacheDao;
      }
    }
  }
}
