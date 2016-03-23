package com.zd.musictorelax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RelaxSHandler extends DefaultHandler{
	private RelaxSBean relaxsItem = null;	
	private RelaxSFeed relaxsFeed;
	private StringBuilder sb = new StringBuilder();
	
	public RelaxSFeed getFeed() {
		return relaxsFeed;
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
		relaxsFeed = new RelaxSFeed();
		relaxsItem = new RelaxSBean();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//(3) ��ʼ�ռ��µı�ǩ������ʱ���������ʷ����
		sb.setLength(0);
		if("M_RelaxationScheme".equals(localName)) {
			relaxsItem = new RelaxSBean();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		//(4)ԭ����characters��ȡֵ���ָ��ڴ�ȡֵ
		String value = sb.toString();
		
		if(RelaxSBean.RID.equals(localName)) {
			relaxsItem.setrID(value);
		} else if(RelaxSBean.RNAME.equals(localName)) {
			relaxsItem.setrName(value);
		} else if(RelaxSBean.RCREATORCODE.equals(localName)) {
			relaxsItem.setrCreatorCode(value);
		} else if(RelaxSBean.RCREATOR.equals(localName)) {
			relaxsItem.setrCreator(value);
		} else if(RelaxSBean.RCREATTIME.equals(localName)) {
			relaxsItem.setrCreatTime(value);
		} else if(RelaxSBean.RDESCRIBE.equals(localName)) {
			relaxsItem.setrDescribe(value);
		} else if(RelaxSBean.RICO.equals(localName)) {
			relaxsItem.setrICO(value);
		} else if(RelaxSBean.RINLAY.equals(localName)) {
			relaxsItem.setrInlay(value);
		} 
		if("M_RelaxationScheme".equals(localName)) {
			 relaxsFeed.addItem(relaxsItem);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}

