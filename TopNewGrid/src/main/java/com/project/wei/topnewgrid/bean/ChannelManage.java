package com.project.wei.topnewgrid.bean;

import android.database.SQLException;
import android.util.Log;

import com.project.wei.topnewgrid.dao.ChannelDao;
import com.project.wei.topnewgrid.db.SQLHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelManage {
	public static ChannelManage channelManage;
	/**
	 * ??????????????б?
	 * */
	public static List<ChannelItem> defaultUserChannels;
	/**
	 * ????????????б?
	 * */
	public static List<ChannelItem> defaultOtherChannels;
	private ChannelDao channelDao;
	/** ?ж??????????????????? */
	private boolean userExist = false;
	static {
		defaultUserChannels = new ArrayList<ChannelItem>();
		defaultOtherChannels = new ArrayList<ChannelItem>();
		defaultUserChannels.add(new ChannelItem(1, "???", 1, 1));
		defaultUserChannels.add(new ChannelItem(2, "???", 2, 1));
		defaultUserChannels.add(new ChannelItem(3, "????", 3, 1));
		defaultUserChannels.add(new ChannelItem(4, "???", 4, 1));
		defaultUserChannels.add(new ChannelItem(5, "???", 5, 1));
		defaultUserChannels.add(new ChannelItem(6, "????", 6, 1));
		defaultUserChannels.add(new ChannelItem(7, "????", 7, 1));
		defaultOtherChannels.add(new ChannelItem(8, "???", 1, 0));
		defaultOtherChannels.add(new ChannelItem(9, "??", 2, 0));
		defaultOtherChannels.add(new ChannelItem(10, "????", 3, 0));
		defaultOtherChannels.add(new ChannelItem(11, "???", 4, 0));
		defaultOtherChannels.add(new ChannelItem(12, "???", 5, 0));
		defaultOtherChannels.add(new ChannelItem(13, "???", 6, 0));
		defaultOtherChannels.add(new ChannelItem(14, "????", 7, 0));
		defaultOtherChannels.add(new ChannelItem(15, "????", 8, 0));
		defaultOtherChannels.add(new ChannelItem(16, "???", 9, 0));
		defaultOtherChannels.add(new ChannelItem(17, "???", 10, 0));
		defaultOtherChannels.add(new ChannelItem(18, "????", 11, 0));
	}

	private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());
		// NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
		return;
	}

	/**
	 * ??????????????
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ChannelManage getManage(SQLHelper dbHelper)throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 * ??????е????
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}
	/**
	 * ???????????
	 * @return ?????????????? ? ???????????????? : ???????????? ;
	 */
	public List<ChannelItem> getUserChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?",new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}
	
	/**
	 * ???????????
	 * @return ?????????????? ? ?????????????? : ?????????? ;
	 */
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()){
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate= new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<ChannelItem>) cacheList;
	}
	
	/**
	 * ????????????????
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(1));
			channelDao.addCache(channelItem);
		}
	}
	
	/**
	 * ?????????????????
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			channelDao.addCache(channelItem);
		}
	}
	
	/**
	 * ??????????????????
	 */
	private void initDefaultChannel(){
		Log.d("deleteAll", "deleteAll");
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}
