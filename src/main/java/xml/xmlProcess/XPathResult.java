package xml.xmlProcess;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XPathResult {
	public static int RES_NOTHING = 0;
	public static int RES_NODE_VALUE = 1;
	public static int RES_NODE_ATTRIBUTE = 2;
	
	private Attribute attrRes;
	private Element contRes;
	private int resType = RES_NOTHING;
	
	public void setResType(Object source) {
		if (source instanceof Attribute) {
			resType = RES_NODE_ATTRIBUTE;
	        this.attrRes = (Attribute)source;
	    }
	    if (source instanceof Element) {
	    	resType = RES_NODE_VALUE;
	        this.contRes = (Element)source;
	    }
	}
	
	public void changeValue(String newVal) {
		if(this.resType == XPathResult.RES_NODE_ATTRIBUTE) {  //modify current node attribute
			this.attrRes.setValue(newVal);
		}
		else if(this.resType == XPathResult.RES_NODE_VALUE) {  //modify current node value
			this.contRes.setText(newVal);
		}
	}
	
	public Attribute getAttrRes() {
		return attrRes;
	}

	public Element getContRes() {
		return contRes;
	}

	public int getResType() {
		return resType;
	}
}
