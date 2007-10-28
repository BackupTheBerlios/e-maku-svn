package net.emaku.tools.gui.chart;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

	public class PieChartScriptlet extends JRDefaultScriptlet
	{
	        public void afterReportInit() throws JRScriptletException
	        {
	                DefaultPieDataset dataset = new DefaultPieDataset();
	                dataset.setValue("Java", new Double(50));
	                dataset.setValue("Visual Basic", new Double(10));
	                dataset.setValue("C/C++", new Double(10));
	                dataset.setValue("PHP", new Double(10));
	                dataset.setValue("Perl", new Double(20));

	                JFreeChart chart = 
	                        ChartFactory.createPieChart3D(
	                                "Pie Chart 3D Demo 1",
	                                dataset,
	                                true,
	                                true,
	                                false
	                                );

	                PiePlot3D plot = (PiePlot3D) chart.getPlot();
	                plot.setStartAngle(290);
	                plot.setDirection(Rotation.CLOCKWISE);
	                plot.setForegroundAlpha(0.5f);
	                plot.setNoDataMessage("No data to display");
	                this.setVariableValue("Chart", new JCommonDrawableRenderer(chart));
	        }
	}

