package common.gui.components;

import java.awt.Component;

import org.jdom.Element;

import common.gui.forms.InstanceFinishingListener;

public interface Couplable extends AnswerListener, InstanceFinishingListener{

	/**
	 * Metodo encargado de retornar el componente
	 * @return
	 */
	public Component getPanel();
	
	/**
	 * Metodo encargado de Limpiar el componente 
	 *
	 */
	public void clean();
	
	/**
	 * Metodo encargado de validar si el componente esta vacio
	 * @param args 
	 * @throws Exception Si el componente esta vacio retorna una excepcion
	 */
	public void validPackage(Element args) throws Exception;
	
	/**
	 * Metodo encargado de retornar un paquete segun la información contenida
	 * en el componente
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Element getPackage(Element args) throws Exception;
	
	/**
	 * Metodo encargado de retornar la información contenida en el componente
	 * @return
	 * @throws VoidPackageException
	 */
	public Element getPackage() throws VoidPackageException;
	
	/**
	 * Metodo encargado de retornar la información del componente formateada para
	 * impresion
	 * @return
	 */
	public Element getPrintPackage();
	
	/**
	 * Metodo encargado de verificar si un componente contiene inforación o esta vacio.
	 * @return
	 */
	public boolean containData();
	
}
