package net.emaku.tools.xml;

import java.awt.Cursor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.gui.ExportBar;
import net.emaku.tools.gui.QueryEditor;
import net.emaku.tools.jar.JarManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRHorizontalFiller;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class TemplateManager {
	
	private static ByteArrayOutputStream bytesReport;
	private static Document documentIn;
	private static Document documentOut;
	private static SAXBuilder builder;
	private static XMLOutputter xout;
	private static String sqlCode;
	private static String file_out;
	private static String name;
	private static JFrame frame;
	
	public static void setFrame(JFrame jframe) {
		frame = jframe;
	}
	
	public static void showReportPreview(InputStream inputS) {
		try {
            Element rootOUT = checkXMLSyntax(inputS);
            if (rootOUT == null) {
            	return;
            }
            
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			bytesReport = new ByteArrayOutputStream();
			xout.output(rootOUT.getDocument(),byteArray);
			//xout.output(rootOUT.getDocument(),new FileOutputStream(file_out+".xml"));
			System.out.println("* Compiling report ..");
			JasperDesign jasperDesign =  JRXmlLoader.load(new ByteArrayInputStream(byteArray.toByteArray()));
			
			System.out.println("* JRXmlLoader passed ....");
			JasperCompileManager.compileReportToStream(jasperDesign,bytesReport);
			System.out.println("* The report has compiled successfully");

			JRResultSetDataSource jrRS = new JRResultSetDataSource(DataBaseManager.getResultSet(frame,sqlCode));
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JRBaseFiller filler = createFiller(jasperReport);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("Fecha",new Date().toString());
			JasperPrint jasperPrint = filler.fill(map,jrRS);
			JasperViewer.viewReport(jasperPrint,false);

		} catch (IOException e) {
			System.out.println("ERROR: File does not exist!");
			System.out.println("Message:" + e.getMessage());
		} catch (JRException e) {
			e.printStackTrace();
		}
	}	

	public static void getGenericReport(String input, boolean preview) {
		 try {
			 File file = new File(input);
			 FileInputStream bytes = new FileInputStream(file);
			 getGenericReport(bytes,preview);
		 } catch(IOException e) {
			 System.out.println("ERROR: An exception occurred at getGenericReport method");
			 e.printStackTrace();
		 }
	}	
				
	public static void getGenericReport(InputStream inputS,boolean preview) {
		
		Element rootOUT = checkXMLSyntax(inputS); 
		if (rootOUT == null) {
			return;
		}
		
		try {	
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			bytesReport = new ByteArrayOutputStream();
			xout.output(rootOUT.getDocument(),byteArray);
			//xout.output(rootOUT.getDocument(),new FileOutputStream(file_out+".xml"));
			System.out.println("* Compiling report ..");
			JasperDesign jasperDesign =  JRXmlLoader.load(new ByteArrayInputStream(byteArray.toByteArray()));
			
			System.out.println("* JRXmlLoader passed ....");
			JasperCompileManager.compileReportToStream(jasperDesign,bytesReport);
			System.out.println("* The report has compiled");
			
			if (preview) {
				JRResultSetDataSource jrRS = new JRResultSetDataSource(DataBaseManager.getResultSet(frame,sqlCode));
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				JRBaseFiller filler = createFiller(jasperReport);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("Fecha",new Date().toString());
				JasperPrint jasperPrint = filler.fill(map,jrRS);
				JasperViewer.viewReport(jasperPrint,false);
			}
			else {
				JarManager.pack(file_out,bytesReport);
				DataBaseManager.addReport(file_out,sqlCode,name);	
			}
		} catch (IOException e) {
			System.out.println("Message:" + e.getMessage());
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public static void showGenericReportPreview(InputStream inputS) {
		try {
			builder = new SAXBuilder(false);
			documentIn = builder.build(inputS);
			inputS.reset();
			Element rootIN = documentIn.getRootElement();
			String sqlCode =  rootIN.getChildText("sqlCode");
			String sentence = DataBaseManager.getQuery(sqlCode);
			if(sentence.equals("NO_SQL")) {
				JOptionPane.showMessageDialog(frame,"ERROR: No sql sentence is associated to the code " + sqlCode);
				return;
			}
			
			if(sentence.indexOf("?") != -1) {
				while(true) {
					QueryEditor editor = new QueryEditor(false,frame,sentence,sqlCode);
					editor.setSize(700,500);
					editor.setLocationRelativeTo(frame);
					editor.setVisible(true);
					sentence = editor.getSQL();
					if(editor.isCanceled()) {
						return;
					}
					if(sentence.indexOf("?") != -1) {
						JOptionPane.showMessageDialog(frame,"ERROR: SQL sentence contains caracter \"?\"." +
								"\nPlease edit it and add correct values to the parameters.");
					} else {
						break;
					}
				}
			}
			
			System.out.println("SENTENCE: " + sentence);
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ResultSet result = DataBaseManager.getResultSet(frame,sentence);
			if (result != null) {
				int size = result.getFetchSize();
				if (size > 0) {
					bytesReport = new ByteArrayOutputStream();
					JasperDesign jasperDesign =  JRXmlLoader.load(inputS);
					JasperCompileManager.compileReportToStream(jasperDesign,bytesReport);
					JRResultSetDataSource jrRS = new JRResultSetDataSource(result);
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					JRBaseFiller filler = createFiller(jasperReport);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("Empresa","Empresa Ejemplo");
					map.put("Nit","000000000-0");
					map.put("Fecha",new Date().toString());
					map.put("Qhatu","Reporte elaborado con eMaku");
					map.put("Url","http://www.qhatu.net");
					JasperPrint jasperPrint = filler.fill(map,jrRS);
					JasperViewer.viewReport(jasperPrint,false);
				} else {
					System.out.println("["+sqlCode+"] : Query is empty");
				}
			} else {
				JOptionPane.showMessageDialog(frame,"The query was empty. No result was returned!");				
			}
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (JRException e) {
			showErrorMessage(e,"JRException","TemplateManager","showGenericReportPreview");
		} catch (JDOMException e) {
			showErrorMessage(e,"JDOMException","TemplateManager","showGenericReportPreview");
		} catch (IOException e) {
			showErrorMessage(e,"IOException","TemplateManager","showGenericReportPreview");
		} catch (SQLException e) {
			showErrorMessage(e,"SQLException","TemplateManager","showGenericReportPreview");
		} catch (Exception e) {
			showErrorMessage(e,"Exception","TemplateManager","showGenericReportPreview");
		}
	}
	
	private static void showErrorMessage(Exception e, String type, String className, String method) {
		String msg = e.getMessage();
		if(msg == null) {
			msg = e.toString();
		}
		System.out.println("ERROR: " + type + " at " + className + " Class (Method: "+ method +")");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		JOptionPane.showMessageDialog(frame,msg,"Error!",
				JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();		
	}
	
	public static int createReport(String path) {
		int error = 0;
		try {
			path += ".xml";
			builder = new SAXBuilder(false);
			documentIn = builder.build(path);
			Element rootIN = documentIn.getRootElement();
			String fileOut =  rootIN.getChildText("code");
			String sqlCode =  rootIN.getChildText("sqlCode");
			String name =  rootIN.getChildText("name");
			ExportBar.addRecord(" Processing " + fileOut + " - Query code: " + sqlCode + "...");
			
			bytesReport = new ByteArrayOutputStream();
			JasperDesign jasperDesign =  JRXmlLoader.load(path);
			JasperCompileManager.compileReportToStream(jasperDesign,bytesReport);
			JarManager.pack(fileOut,bytesReport);
			
			if (!fileOut.equals("REP0000") && !fileOut.equals("CRE0000")) {
			    error = DataBaseManager.addReport(fileOut,sqlCode,name);
			}
		} catch (JRException e) {
			ExportBar.addRecord("ERROR: " + e.toString());
			e.printStackTrace();
		} catch (JDOMException e) {
			ExportBar.addRecord("ERROR: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			ExportBar.addRecord("ERROR: " + e.toString());
			e.printStackTrace();
		}
		return error;
	}
	
	public static JRBaseFiller createFiller(JasperReport jasperReport) throws JRException {
		JRBaseFiller filler = null;

		switch (jasperReport.getPrintOrder()) {
			case JRReport.PRINT_ORDER_HORIZONTAL :
				filler = new HorizontalFiller(jasperReport);
				break;
			case JRReport.PRINT_ORDER_VERTICAL :
				filler = new VerticalFiller(jasperReport);
				break;
		}
		return filler;
	}
	
	public static Element checkXMLSyntax(String xmlFile) {
		Element rootOUT = null;
		try {
			 File file = new File(xmlFile);
			 FileInputStream bytes = new FileInputStream(file);
			 rootOUT = checkXMLSyntax(bytes);
			 if (rootOUT == null)
				 return null;
			} catch(IOException ex) {
			}
			
		return rootOUT;
	}	
	
	public static Element checkXMLSyntax(InputStream inputS) {
		Element rootOUT;	
		
		try { 	
			builder = new SAXBuilder(false);
			documentIn = builder.build(inputS);
			inputS.reset();
			documentOut = builder.build("template.xml");
			
			Element rootIN = documentIn.getRootElement();
			rootOUT = documentOut.getRootElement();
			file_out =  rootIN.getChildText("code");
			sqlCode =  rootIN.getChildText("sqlCode");
			name =  rootIN.getChildText("name");
			System.out.println("* Report code: " +file_out + " Query code: " + sqlCode);
			Element fieldSet = rootIN.getChild("field_set");
			
			xout = new XMLOutputter();
			xout.setFormat(Format.getPrettyFormat());
			
			List listFields = fieldSet.getChildren();
			Iterator it = listFields.iterator();
			int i=0;
			//System.out.println("* Report Fields");
			//System.out.println("* =====================");
			Iterator itStyle = rootOUT.getChildren("style").iterator();
			while (itStyle.hasNext()) {
				itStyle.next();
				i++;
			}
			Iterator itParams = rootOUT.getChildren("parameter").iterator();
			while (itParams.hasNext()) {
				itParams.next();
				i++;
			}
			i = i*2;
			while (it.hasNext()) {
				Element element = (Element) it.next();
				rootOUT.addContent(i,(Element) element.clone());
				//System.out.println("* - " +element.getAttributeValue("name"));
				i++;
			}
			//System.out.println("* =====================");
			Element title = rootIN.getChild("title_set").getChild("text");
			rootOUT.getChild("pageHeader").getChild("band").getChild("staticText").addContent((Element)title.clone());
			System.out.println("* Report Title: " + title.getText());
			
			List listColumn = rootIN.getChild("column_header_set").getChildren(); 
			it = listColumn.iterator();
			Element columnHeader = rootOUT.getChild("columnHeader");
			while (it.hasNext()) {
				Element element = (Element) it.next();
				columnHeader.getChild("band").addContent((Element) element.clone());
			}
			it = null;
			it = rootIN.getChild("field_set").getChildren().iterator();
			Iterator itFields = rootIN.getChild("column_header_set").getChildren().iterator();
			
			while (it.hasNext()) {
				Element element = (Element) it.next();
				Element textField = new Element("textField");
				Element reportElement = (Element) ((Element)itFields.next()).getChild("reportElement").clone();
				Element textFieldExpresion = new Element("textFieldExpression");
				
				reportElement.getAttribute("y").setValue("0");
				reportElement.removeAttribute("style");
				textFieldExpresion.setAttribute("class",element.getAttributeValue("class"));
				CDATA cdata = new CDATA("$F{"+element.getAttributeValue("name")+"}");
				textFieldExpresion.addContent(cdata);
				
				textField.addContent((Element)reportElement.clone());
				textField.addContent(new Element("textElement"));
				textField.addContent(textFieldExpresion);
				rootOUT.getChild("detail").getChild("band").addContent(textField);
			}
			/* it = rootIN.getChild("footer_set").getChildren().iterator();
			
			while (it.hasNext()) {
				Element element = (Element) ((Element) it.next()).clone();
				rootOUT.getChild("pageFooter").getChild("band").addContent(element);
			} */
			
			System.out.println("* The xml file has check ");

			XMLOutputter out = new XMLOutputter();
			out.setFormat(Format.getPrettyFormat());
			out.output(rootOUT, System.out);
			
			
		} catch (JDOMException e) {
			System.out.println("The xml file it's malformed");
			System.out.println("Message:" + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("From checkXMLSyntax -> Not exist file");
			System.out.println("Message:" + e.getMessage());
			return null;
		}
		
		return rootOUT;
	}
	
}

class HorizontalFiller extends JRHorizontalFiller {
	protected HorizontalFiller(JasperReport arg0) throws JRException {
		super(arg0);
	}
}

class VerticalFiller extends JRVerticalFiller {
	protected VerticalFiller(JasperReport arg0) throws JRException {
		super(arg0);
	}
}