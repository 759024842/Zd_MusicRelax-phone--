package com.zd.musictorelax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RelaxSSHandler extends DefaultHandler{
	private RelaxSSBean relaxssItem = null;	
	private RelaxSSFeed relaxssFeed;
	private StringBuilder sb = new StringBuilder();
	
	public RelaxSSFeed getFeed() {
		return relaxssFeed;
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
		relaxssFeed = new RelaxSSFeed();
		relaxssItem = new RelaxSSBean();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//(3) ��ʼ�ռ��µı�ǩ������ʱ���������ʷ����
		sb.setLength(0);
		if("M_RelaxationSchemeSong".equals(localName)) {
			relaxssItem = new RelaxSSBean();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		//(4)ԭ����characters��ȡֵ���ָ��ڴ�ȡֵ
		String value = sb.toString();
		
		if(RelaxSSBean.RSID.equals(localName)) {
			relaxssItem.setRsID(value);
		} else if(RelaxSSBean.SID.equals(localName)) {
			relaxssItem.setsID(value);
		} else if(RelaxSSBean.RID.equals(localName)) {
			relaxssItem.setrID(value);
		} else if(RelaxSSBean.ENABLE.equals(localName)) {
			relaxssItem.setEnable(value);
		} 
		if("M_RelaxationSchemeSong".equals(localName)) {
			 relaxssFeed.addItem(relaxssItem);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}


