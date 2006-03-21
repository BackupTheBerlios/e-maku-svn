/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS HtmlProperties v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar la estructura de datos
* referente a las caracteristicas visuales de un reporte.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.report;

public class HtmlProperties {

 String globalBackground = "#FFFFFF";
 String tableHeaderBackground = "#FFFFFF";
 String tableHeaderForeground = "#000000";
 int tableHeaderFontSize = 12;

 String tableHeaderFontStyle = "arial";
 String tableCellBackground = "#FFFFFF";
 String tableCellForeground = "#000000";
 int tableCellFontSize = 12;

 String tableCellFontStyle = "arial";
 int tableBorder = 1;
 int cellPadding = 0;
 int spacePadding = 0;
 String tableWidth = "100%";
 String theTitle = "";

 String headerFontSize = "12";
 String headerFontStyle = "arial";
 String headerFontColor = "#000000";

 String footerFontSize = "12";
 String footerFontStyle = "arial";
 String footerFontColor = "#000000";

 String HEADER = "<center><hr width=100% size=1></center>";
 String FOOTER = "<center><hr width=100% size=1></center>";

 String[] stylesTC = {"<table border=1>"};
 String[] stylesTHB = {"#FFFFFF"};
 String[] stylesTCB = {"#FFFFFF"};
 String[] stylesH = {"<center><hr width=100% size=1></center>"};
 String[] stylesF = {"<center><hr width=100% size=1></center>"};

 public HtmlProperties() {
  }

 public HtmlProperties(int type) {
   type = 0;
   tableHeaderBackground = stylesTHB[type];
   tableCellBackground = stylesTCB[type];
   HEADER = stylesH[type];
   FOOTER = stylesF[type];
  }

 public HtmlProperties(String bGColor,String cCellFont,String thb, String thfontColor, String tcb,String h,String f) {
   
   globalBackground = bGColor;
   tableCellForeground = cCellFont;
   tableHeaderBackground = thb;
   tableHeaderForeground = thfontColor;
   tableCellBackground = tcb;
   HEADER = h;
   FOOTER = f;
  }

 public void setSettings(int thsf,int tcsf,String thfs,String tcfs, 
                         int tableB,int cellP,int spaceP,String width) {

   tableHeaderFontSize = thsf;
   tableCellFontSize = tcsf;
   tableHeaderFontStyle = thfs;
   tableCellFontStyle = tcfs;
   tableBorder = tableB;
   cellPadding = cellP;
   spacePadding = spaceP;
   tableWidth = width; 
  }

 public void setHeaderSettings(String title,String hFontStyle,String hFontS,String hFColor) {

   theTitle = title;
   headerFontStyle = hFontStyle;
   headerFontSize = hFontS;
   headerFontColor = hFColor;
  }

 public void setFooterSettings(String fFontStyle,String fFontS,String fFColor) {

   footerFontStyle = fFontStyle;
   footerFontSize = fFontS;
   footerFontColor = fFColor;
  }

 public String getGlobalProperties() {
   return "body {\n      background-color:" +  globalBackground + ";\n      text-align:center;\n}\n\n";
  }

 public String getThProperties() {
   return "th {\n    background-color:" +  tableHeaderBackground + ";\n    color:" + tableHeaderForeground 
              + ";\n    font-family:courier;\n    font-size:" + tableHeaderFontSize + "pt;\n" + "    text-align:center;\n"
              + "    font-weight:bold;\n    padding:" + cellPadding + "pt;\n}\n\n";
  }

 public String getHeaderAttrib() {
   return "p.header {\n          text-align:justify;\n          font-size:" + headerFontSize 
          + "pt;\n          font-family:" + headerFontStyle + ";\n          color:" + headerFontColor + ";\n}\n\n";
  }

 public String getDataAttrib() {

   return "td.data {\n         background-color:" + tableCellBackground + ";\n         color:" 
          + tableCellForeground + ";\n         font-family:" + tableHeaderFontStyle 
          + ";\n         font-size:" + tableCellFontSize + "pt;\n"
          + "         text-align:center;\n         font-weight:normal;\n         padding:" + cellPadding + "pt;\n}\n\n";
  }

 public String getLinksAttrib() {

   return "td.links {\n          background-color:" + globalBackground + ";\n          font-family:courier;\n          font-size:8pt;\n"
          + "          text-align:center;\n          font-weight:normal;\n}\n\n";
  }

 public String getFooterAttrib() {
   return "p.footer {\n          text-align:justify;\n          font-size:" + footerFontSize
          + "pt;\n          font-family:" + footerFontStyle + ";\n          color:" + footerFontColor
          + ";\n}\n\n"; 
  }

 public String getHeader() {
   return HEADER;
  }

 public String getFooter() {
   return FOOTER;
  }

 public int getTableBorder() {
    return tableBorder;
  }

 public int getSpacePadding() {
    return spacePadding;
 }

 public String getTableWidth() {
    return tableWidth;
 }

 public String getTheTitle() {
    return theTitle;
 }

} //Fin de la Clase
