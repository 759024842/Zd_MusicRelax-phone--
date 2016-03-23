package com.zd.musictorelax.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class Dao {

	boolean isFirstRun = true;
	Context context;
	SQLiteDatabase db = null;

	public Dao(Context context) {
		this.context = context;
		this.db = SQLiteDatabase.openDatabase(initDb.Path + initDb.dbName,null, SQLiteDatabase.OPEN_READWRITE);

	}

	/**
	 * 判断表是否存�?true 存在 false不存�?
	 * 
	 * @param tablename
	 * @return
	 */
	public boolean tableExit(String tablename) {
		if (tablename == null) {
			return false;
		} else {
			String sql = "select * from " + tablename + ";";

			Cursor cursor = null;
			try {

				cursor = db.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count >= 0) {
						return true;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
		}
		return true;
	}

	/**
	 * 添加数据 参数为表�?contentvalues
	 * 
	 * @param tablename
	 * @param cv
	 */
	public long add(String tablename, ContentValues cv) {
		try {
			if (tableExit(tablename)) {
				return db.insertOrThrow(tablename, null, cv);
			}

		} catch (SQLException e) {
			// TODO: handle exception
			Toast.makeText(context, tablename + "添加失败", 1).show();
			return -1;
		}
		return -1;
	}

	/**
	 * 提供更新功能
	 * 
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		try {
			if (tableExit(table)) {
				return db.update(table, values, whereClause, whereArgs);
			}

		} catch (SQLException e) {
			// TODO: handle exception
			Toast.makeText(context, table + "更新失败", 1).show();
			return -1;
		}
		return -1;

	}

	/**
	 * 删除操作 用法同系统删除形�?成功返回影响行数 失败返回-1
	 * 
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		try {
			return db.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}

	}

	/**
	 * 查询操作 成功返回cursor对象 失败返回null
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor select(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		try {
			if (tableExit(table)) {
				return db.query(table, columns, selection, selectionArgs,
						groupBy, having, orderBy, limit);
			}

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return null;

	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
	}

	/**
	 * 返回db
	 * 
	 * @return
	 */
	public SQLiteDatabase getDb() {
		return db;
	}

}
