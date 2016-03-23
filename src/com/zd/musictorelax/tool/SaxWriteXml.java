package com.zd.musictorelax.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlSerializer;
import android.os.Environment;
import android.util.Xml;

public class SaxWriteXml {
	public void writeToXml(String str,String filePat) {
		OutputStream out = null;
		try {
			String dirPath = Environment.getExternalStorageDirectory().toString() + "/xml/";
			File file = new File(dirPath);
			if (!file.exists()) {
				file.mkdir();
				}
			File f = new File(file, filePat);
			if (!f.exists()) {
				f.createNewFile();
			}
			out = new FileOutputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		OutputStreamWriter outWriter = new OutputStreamWriter(out);
		try {
			outWriter.write(str);
			outWriter.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public  String savexml(ArrayList<HashMap<String,Object>> data) {
		 //xml解析器
		 XmlSerializer xml=Xml.newSerializer();
	     StringWriter writer = new StringWriter();  
	     try 
	     {
		 //设置xml文件的输出方向
		 xml.setOutput(writer);
		 //设置xml的开始文档内容及编码格式
		 xml.startDocument("utf-8", true);
		 //设置xml的开始节点
		 xml.startTag(null, "root");
		 
         for(int i=0;i<data.size();i++){
		 xml.startTag(null, "Table");
		 
		 xml.startTag(null, "sName");
		 xml.text(data.get(i).get("sName")+"");
		 xml.endTag(null, "sName");
		  
		 xml.startTag(null, "splayTime");
		 xml.text(data.get(i).get("splayTime")+"");
		 xml.endTag(null, "splayTime");

		 xml.startTag(null, "sFormat");
		 xml.text(data.get(i).get("sFormat")+"");
		 xml.endTag(null, "sFormat");

		 xml.endTag(null, "Table");
         }
		 xml.endTag(null, "root");
		 //结束文档
		 xml.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			return writer.toString();  
	}

}
