package net.emaku.tools.gui.chart;

import java.util.Iterator;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartScriptlet extends JRDefaultScriptlet {
	
	final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	String[] variable = new String[2];
	
    public void  afterDetailEval() throws JRScriptletException {
    	Double var = Double.valueOf(this.getFieldValue(variable[0]).toString());
    	String field = this.getFieldValue(variable[1]).toString();
    	dataset.addValue(var,field, "");
    }
	
    public void afterReportInit() throws JRScriptletException {
    	Iterator it = fieldsMap.keySet().iterator();
    	int i = 0;
        while (it.hasNext()) {
            variable[i] = it.next().toString();
            i++;
        }
    	
        final JFreeChart chart = ChartFactory.createBarChart3D(
                "Utilidad por Línea",      // chart title
                "Líneas",               // domain axis label
                "Porcentaje",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
            );
        
        final CategoryPlot plot = chart.getCategoryPlot();        
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        final BarRenderer r = (BarRenderer) renderer;
        r.setMaximumBarWidth(0.05);
        
        chart.setTextAntiAlias(true);
    	this.setVariableValue("Chart", new JCommonDrawableRenderer(chart));
    }
       
}
