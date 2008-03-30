package client.gui.forms;

import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.GrayFilter;

import java.awt.Component;
import java.net.URL;

import java.util.Locale;
import java.util.TreeMap;

    /**
     * Esta clase se encarga de adicionar las banderas en el combo de idiomas
     * 
     *       
     * @author Julien Ponge
     */
    final class FlagRenderer extends JLabel implements ListCellRenderer
    {
        private static final long serialVersionUID = 3832899961942782769L;

        /** Cache de Iconos. */    
        private TreeMap<String, ImageIcon> icons = new TreeMap<String, ImageIcon>();
   
        /** Cache de iconos grises. */
        private TreeMap<String, ImageIcon> grayIcons = new TreeMap<String, ImageIcon>();

        public FlagRenderer()
        {
            setOpaque(true);
        }

        /**
         * Retorna una celda personalizada
         * 
         * @param list La lista
         * @param value El objeto
         * @param index El indice
         * @param isSelected verdadero si esta seleccionada
         * @param cellHasFocus Descripcion del parametro
         * @return La celda
         */
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
        {	
            // We put the label
            String langCode = (String) value;
            String lang = langCode.substring(0,2);
            String country = langCode.substring(3,5);
            
            
            Locale locale = new Locale(lang,country);
            setText(locale.getDisplayLanguage() + " (" + locale.getCountry() + ")");       
            
            if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
            }
            else {
                setForeground(list.getForeground());
                setBackground(list.getBackground());
            }
            // Colocamos el icono de la bandera

            if (!icons.containsKey(langCode))
            {
            	ImageIcon icon;
            	URL url = this.getClass().getResource("/icons/ico_" + langCode + ".png");
            	if (url != null) {
            		icon = new ImageIcon(url);
            	}
            	else {
            		icon = new ImageIcon(this.getClass().getResource("/icons/ico_" + "NOFLAG.png"));
            	}
            	icons.put(langCode, icon);
            	icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
            	grayIcons.put(langCode, icon);	                
            }
            
            if (isSelected || index == -1) {
                setIcon((ImageIcon) icons.get(langCode));
            }
            else {
                setIcon((ImageIcon) grayIcons.get(langCode));
            }

            return this;
        }
}
