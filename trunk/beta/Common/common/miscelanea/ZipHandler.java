package common.miscelanea;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jdom.Element;

import com.Ostermiller.util.Base64;

public class ZipHandler {
	
	private ByteArrayOutputStream bosz;
	private ZipOutputStream zip;
	
	public ZipHandler (ByteArrayOutputStream os , String zipEntryName) {
		try {
			bosz = new ByteArrayOutputStream();
	        zip = new ZipOutputStream (bosz);
	        zip.setLevel( 9 );
	        zip.setMethod( ZipOutputStream.DEFLATED );
	        
	        ZipEntry entry = new ZipEntry( zipEntryName );
	        byte[] buf = os.toByteArray();
			zip.putNextEntry( entry );
			zip.write( buf , 0, buf.length );
	        zip.closeEntry();
	        os.close();
	        zip.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ZipHandler () {}
	
	public Element getElementDataEncode(String ElementName) throws IOException {

		Element element = new Element(ElementName);
		
		ByteArrayOutputStream encoding = new ByteArrayOutputStream();
		Base64.encode(new ByteArrayInputStream(bosz.toByteArray()),encoding);
        encoding.close();
        bosz.close();
        element.setText(new String(encoding.toByteArray()));
        
		return element;
	}
	
	public byte[] getDataDecode(String Base64Data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Base64.decode(Base64Data.getBytes(),baos);
		baos.close();

		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
		ByteArrayOutputStream baosdoc = new ByteArrayOutputStream(); 
		zip.getNextEntry();
		byte[] buf = new byte[256];
        int len;
        while ((len = zip.read(buf)) >= 0) {
            baosdoc.write(buf, 0, len);
        }
		baosdoc.close();
		zip.closeEntry();
		zip.close();
		return baosdoc.toByteArray();
	}
	
	public ByteArrayInputStream getDataDecodeInputStream(String Base64Data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Base64.decode(Base64Data.getBytes(),baos);
		baos.close();
		
		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
		
		ByteArrayOutputStream baosdoc = new ByteArrayOutputStream(); 
		zip.getNextEntry();
		byte[] buf = new byte[256];
        int len;
        while ((len = zip.read(buf)) >= 0) {
            baosdoc.write(buf, 0, len);
        }
		baosdoc.close();
		zip.closeEntry();
		zip.close();
		return new ByteArrayInputStream(baosdoc.toByteArray());
	}
}