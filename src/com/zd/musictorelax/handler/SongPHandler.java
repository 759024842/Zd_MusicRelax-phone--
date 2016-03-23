package com.zd.musictorelax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SongPHandler extends DefaultHandler{
	
	
	private SongPBean songPItem = null;	
	private SongPFeed songPFeed;
	private StringBuilder sb = new StringBuilder();
	
	public SongPFeed getFeed() {
		return songPFeed;
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
		songPFeed = new SongPFeed();
		songPItem = new SongPBean();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//(3) 开始收集新的标签的数据时，先清空历史数据
		sb.setLength(0);
		if("Table".equals(localName)) {
			songPItem = new SongPBean();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		//(4)原来在characters中取值，现改在此取值
		String value = sb.toString();
		
		
	    if(SongPBean.SNAME.equals(localName)) {
			songPItem.setsName(value);
		} 
		else if(SongPBean.SFORMAT.equals(localName)) {
			songPItem.setsFormat(value);
		} else if(SongPBean.SPLAYTIME.equals(localName)) {
			songPItem.setSplayTime(value);
		} 
		
		if("Table".equals(localName)) {
			 songPFeed.addItem(songPItem);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

}
