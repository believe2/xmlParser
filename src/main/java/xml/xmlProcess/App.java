package xml.xmlProcess;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	StaxParser sp = new StaxParser();
    	sp.loadXmlFromFile("D:\\Spring\\workspace\\xmlProcess\\test.xml");
    	sp.setValByXPathList("//Config/FormBatch2==<FormBatch2 startEvent=\"step.start\" stopEvent=\"step.down\"><a>stupid</a></FormBatch2>");
    	String result = sp.getCurXmlContent();
    	System.out.println(result);
    	sp.delByXPathList("//Config/FormBatch2");
    	result = sp.getCurXmlContent();
    	System.out.println(result);
    }
}
