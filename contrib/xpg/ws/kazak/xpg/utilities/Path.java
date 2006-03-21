/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS Path v 0.1
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/10/01
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws
*          Gustavo Gonzalez - xtingray@kazak.ws
*          Diego Armando Gomez
*/

package ws.kazak.xpg.utilities;

import java.io.File;

public class Path {
	private static String pathxpg="";
	/**
	 * 
	 */
	public Path() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String getPathxpg() {
		try {
			File tmp=File.createTempFile("xpg",".cfg");
			pathxpg=tmp.getParent();
			return pathxpg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pathxpg;
	}

}
