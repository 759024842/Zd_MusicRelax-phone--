package com.zd.musictorelax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SongHandler extends DefaultHandler{
	
	
	private SongBean songItem = null;	
	private SongFeed songFeed;
	private StringBuilder sb = new StringBuilder();
	
	public SongFeed getFeed() {
		return songFeed;
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
		songFeed = new SongFeed();
		songItem = new SongBean();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//(3) ��ʼ�ռ��µı�ǩ������ʱ���������ʷ����
		sb.setLength(0);
		if("M_song".equals(localName)) {
			songItem = new SongBean();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		//(4)ԭ����characters��ȡֵ���ָ��ڴ�ȡֵ
		String value = sb.toString();
		
		if(SongBean.SID.equals(localName)) {
			songItem.setsID(value);
		} else if(SongBean.SNO.equals(localName)) {
			songItem.setsNo(value);
		} else if(SongBean.SNAME.equals(localName)) {
			songItem.setsName(value);
		} else if(SongBean.KID.equals(localName)) {
			songItem.setkID(value);
		} else if(SongBean.SFORMAT.equals(localName)) {
			songItem.setsFormat(value);
		} else if(SongBean.SPLAYTIME.equals(localName)) {
			songItem.setSplayTime(value);
		} else if(SongBean.SDESCRIBE.equals(localName)) {
			songItem.setsDescribe(value);
		} else if(SongBean.SENABLE.equals(localName)) {
			songItem.setsEnable(value);
		} else if(SongBean.SGROUP.equals(localName)) {
			songItem.setsGroup(value);
		}
		if("M_song".equals(localName)) {
			 songFeed.addItem(songItem);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}