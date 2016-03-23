package com.zd.musictorelax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SongKHandler extends DefaultHandler{
	private SongKBean songkItem = null;	
	private SongKFeed songkFeed;
	private StringBuilder sb = new StringBuilder();
	
	public SongKFeed getFeed() {
		return songkFeed;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		songkFeed = new SongKFeed();
		songkItem = new SongKBean();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//(3) 开始收集新的标签的数据时，先清空历史数据
		sb.setLength(0);
		if("M_songKind".equals(localName)) {
			songkItem = new SongKBean();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		//(4)原来在characters中取值，现改在此取值
		String value = sb.toString();
		
		if(SongKBean.KID.equals(localName)) {
			songkItem.setkID(value);
		} else if(SongKBean.KNAME.equals(localName)) {
			songkItem.setkName(value);
		} else if(SongKBean.KDESCRIBE.equals(localName)) {
			songkItem.setkDescribe(value);
		} else if(SongKBean.KICO.equals(localName)) {
			songkItem.setkICO(value);
		} else if(SongKBean.KGROUP.equals(localName)) {
			songkItem.setkGroup(value);
		} 
		if("M_songKind".equals(localName)) {
			 songkFeed.addItem(songkItem);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}

