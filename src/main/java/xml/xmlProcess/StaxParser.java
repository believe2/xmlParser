package xml.xmlProcess;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class StaxParser {
	
	public static String CHAR_SPLIT_ITEM = ";";
	public static String CHAR_ITEM_EQUAL = "==";
	
	private Document document;
	private XPathFactory xFactory;
	
	public StaxParser() {
		this.xFactory = XPathFactory.instance();
	}
	
	public void loadXmlFromString(String strContent) {
		SAXBuilder saxBuilder = new SAXBuilder();
        try {
       	    InputStream stream = new ByteArrayInputStream(strContent.getBytes("UTF-8"));
			this.document = saxBuilder.build(stream);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String loadXmlFromFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}
			this.loadXmlFromString(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public XPathResult getValByXPath(String xPath) {
		 XPathExpression<Object> exp = this.xFactory.compile(xPath);
		 Object obj = exp.evaluateFirst(this.document);
		 XPathResult getRes = new XPathResult();
		 getRes.setResType(obj);
		 
		 return getRes;
	}
	
	public void setValByXPath(String xPath) {
		String[] listItem = xPath.split(CHAR_ITEM_EQUAL);
		if(listItem.length != 2) {
			System.out.println("Exception : " + xPath + " illegal");
			return;
		}
		//full xPath query
		XPathResult XpathResult = this.getValByXPath(listItem[0]);
		if(XpathResult.getResType() == XpathResult.RES_NOTHING) {  //add new element
			String[] newTarget = this.retrieveFinalNode(listItem[0]);
			System.out.println("# New item");
			System.out.println("parent node = " + newTarget[0] + ", new node = " + newTarget[1]);
			//get parent node
			XPathResult XpathResultParent = this.getValByXPath(newTarget[0]);
			if(XpathResultParent.getResType() != XpathResultParent.RES_NODE_VALUE) {
				System.out.println("Exception : " + newTarget[0] + " illegal");
				return;
			}
			Element eleParent = XpathResultParent.getContRes();
			if(newTarget[1].contains("@")) {  //add attribute
				Attribute newAttr = new Attribute(newTarget[1].replace("@", ""), listItem[1]);
				eleParent.setAttribute(newAttr);
			}
			else {  //add new node
				if(listItem[1].startsWith("<")) {  //add complicated node
					System.out.println("add new complicated node = " + listItem[1]);
					StaxParser spSubNode = new StaxParser();
					spSubNode.loadXmlFromString(listItem[1]);
					XPathResult getRes = spSubNode.getValByXPath("//" + newTarget[1]);
					if(getRes.getResType() == XPathResult.RES_NODE_VALUE) {
						Element getSubNode = getRes.getContRes();
						eleParent.addContent(getSubNode.clone());  //need clone avoiding hash conflict
					}
				}
				else {  //add simple node with only value
					Element newEle = new Element(newTarget[1]);
					newEle.setText(listItem[1]);
					eleParent.addContent(newEle);
					System.out.println("add new simple node = " + listItem[1]);
				}
			}
		}
		else {  //modify existing node / attribute value
			XpathResult.changeValue(listItem[1]);
		}
	}
	
	public void delByXPath(String xPath) {
		String[] newTarget = this.retrieveFinalNode(xPath);
		String parentXpath = newTarget[0];
		String delTarget = newTarget[1];
		
		XPathResult XpathResultParent = this.getValByXPath(parentXpath);
		if(XpathResultParent.getResType() != XpathResultParent.RES_NODE_VALUE) {
			System.out.println("Exception : " + parentXpath + " illegal");
			return;
		}
		
		Element eleParent = XpathResultParent.getContRes();
		if(delTarget.contains("@")) {  //delete attribute
			eleParent.removeAttribute(delTarget.replace("@", ""));
		}
		else {  //delete node
			eleParent.removeChildren(delTarget);
		}
	}
	
	public String[] retrieveFinalNode(String xPath) {
		String[] res = {"", ""};
		String[] listItem = xPath.split("/");
		
		res[1] = listItem[listItem.length - 1];
		
		int index = 0;
		while(index < listItem.length - 1) {
			res[0] = res[0] + listItem[index];
			if(index < listItem.length - 2) {
				res[0] = res[0] + "/";
			}
			index = index + 1;
		}
		return res;
	}
	
	public String getCurXmlContent() {
		XMLOutputter xmlOutput = new XMLOutputter();
		String result = null;
		 
        xmlOutput.setFormat(Format.getPrettyFormat());
        result = xmlOutput.outputString(this.document);
		return result;
	}

	public void setValByXPathList(String rawContent) {
		String[] listXPath = rawContent.split(this.CHAR_SPLIT_ITEM);
		int index = 0;
		while(index < listXPath.length) {
			this.setValByXPath(listXPath[index]);
			index = index + 1;
		}
	}
	public void delByXPathList(String rawContent) {
		String[] listXPath = rawContent.split(this.CHAR_SPLIT_ITEM);
		int index = 0;
		while(index < listXPath.length) {
			this.delByXPath(listXPath[index]);
			index = index + 1;
		}
	}
}
