/*
 * $Id: Compiler.java,v 1.111.2.2 2006/01/06 21:00:51 jponge Exp $
 * IzPack - Copyright 2001-2006 Julien Ponge, All Rights Reserved.
 * 
 * http://www.izforge.com/izpack/
 * http://developer.berlios.de/projects/izpack/
 * 
 * Copyright 2001 Johannes Lehtinen
 * Copyright 2002 Paul Wilkinson
 * Copyright 2004 Gaganis Giorgos
 *
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import com.izforge.izpack.CustomData;
import com.izforge.izpack.GUIPrefs;
import com.izforge.izpack.Info;
import com.izforge.izpack.Panel;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.VariableSubstitutor;

/**
 * The IzPack compiler class. This is now a java bean style class that can be
 * configured using the object representations of the install.xml
 * configuration. The install.xml configuration is now handled by the
 * CompilerConfig class.
 * 
 * @see CompilerConfig
 * 
 * @author Julien Ponge
 * @author Tino Schwarze
 * @author Chadwick McHenry
 */
public class Compiler extends Thread
{
    /** The IzPack version. */
    public final static String IZPACK_VERSION = "3.8.1";

    /** The IzPack home directory. */
    public static String IZPACK_HOME = ".";

    /** The base directory. */
    protected String basedir;

    /** The installer kind. */
    protected String kind;

    /** The output jar filename. */
    protected String output;

    /** Collects and packs files into installation jars, as told. */
    private Packager packager = null;

    /** Error code, set to true if compilation succeeded. */
    private boolean compileFailed = true;

    /** Key/values which are substituted at compile time in the install data */
    private Properties properties;

    /** Replaces the properties in the install.xml file prior to compiling */
    private VariableSubstitutor propertySubstitutor;

    /**
     * Set the IzPack home directory
     * @param izHome - the izpack home directory
     */
    public static void setIzpackHome(String izHome)
    {
        IZPACK_HOME = izHome;
    }

    /**
     * The constructor.
     * 
     * @param basedir The base directory.
     * @param kind The installer kind.
     * @param output The installer filename.
     * @throws CompilerException
     */
    public Compiler(String basedir, String kind, String output) throws CompilerException
    {
        this(basedir,kind,output,"default");
    }

    /**
     * The constructor.
     * 
     * @param basedir The base directory.
     * @param kind The installer kind.
     * @param output The installer filename.
     * @param compr_format The format which should be used for the packs.
     * @throws CompilerException
     */
    public Compiler(String basedir, String kind, String output, String compr_format) throws CompilerException
    {
        this(basedir,kind,output, compr_format, -1);
    }

    /**
     * The constructor.
     * 
     * @param basedir The base directory.
     * @param kind The installer kind.
     * @param output The installer filename.
     * @param compr_format The format which should be used for the packs.
     * @param compr_level Compression level to be used if supported.
     * @throws CompilerException
     */
    public Compiler(String basedir, String kind, String output, 
            String compr_format, int compr_level) throws CompilerException
    {
        // Default initialisation
        this.basedir = basedir;
        this.kind = kind;
        this.output = output;

        // initialize backed by system properties
        properties = new Properties(System.getProperties());
        propertySubstitutor = new VariableSubstitutor(properties);

        // add izpack built in property
        setProperty("izpack.version", IZPACK_VERSION);
        setProperty("basedir", basedir);

        packager = new Packager(compr_format, compr_level);
        packager.getCompressor().setCompiler(this);
    }

    
    /**
     * Retrieves the packager listener
     */
    public PackagerListener getPackagerListener()
    {
        return packager.getPackagerListener();
    }
    /**
     * Sets the packager listener.
     * 
     * @param listener The listener.
     */
    public void setPackagerListener(PackagerListener listener)
    {
        packager.setPackagerListener(listener);
    }

    /**
     * Access the installation kind.
     * @return the installation kind.
     */
    public String getKind()
    {
        return kind;
    }
    /**
     * Get the packager variables.
     * @return the packager variables
     */
    public Properties getVariables()
    {
        return packager.getVariables();
    }

    /** Compiles. */
    public void compile()
    {
        start();
    }

    /** The run() method. */
    public void run()
    {
        try
        {
            createInstaller(); // Execute the compiler - may send info to
            // System.out
        }
        catch (CompilerException ce)
        {
            System.out.println(ce.getMessage() + "\n");
        }
        catch (Exception e)
        {
            if (Debug.stackTracing())
            {
                e.printStackTrace();
            }
            else
            {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Compiles the installation.
     * 
     * @exception Exception Description of the Exception
     */
    public void createInstaller() throws Exception
    {
        // Add the class files from the chosen compressor.
        if( packager.getCompressor().getContainerPaths() != null )
        {
            String [] containerPaths = packager.getCompressor().getContainerPaths();
            String [][] decoderClassNames = packager.getCompressor().getDecoderClassNames();
            for( int i = 0; i < containerPaths.length; ++i)
            {
                URL compressorURL = null;
                if( containerPaths[i] != null )
                    compressorURL = findIzPackResource(containerPaths[i],"pack compression Jar file");
                if( decoderClassNames[i] != null && decoderClassNames[i].length > 0)
                    addJarContent(compressorURL, Arrays.asList(decoderClassNames[i]));
            }
            
            
        }
  
        // We ask the packager to create the installer
        packager.createInstaller(new File(output));
        this.compileFailed = false;
    }

    public boolean wasSuccessful()
    {
        return !this.compileFailed;
    }

    public String replaceProperties(String value) throws CompilerException
    {
        return propertySubstitutor.substitute(value, "at");
    }

    public void setGUIPrefs(GUIPrefs prefs)
    {
        packager.setGUIPrefs(prefs);
    }
    public void setInfo(Info info) throws Exception
    {
        packager.setInfo(info);
    }

    /**
     * Get the install packager.
     * @return the install packager.
     */
    public Packager getPackager()
    {
        return packager;
    }
    /**
     * Get the properties currently known to the compileer.
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * Get the value of a property currerntly known to izpack.
     * 
     * @param name the name of the property
     * @return the value of the property, or null
     */
    public String getProperty(String name)
    {
        return properties.getProperty(name);
    }

    /**
     * Add a name value pair to the project property set. Overwriting any existing value.
     * 
     * @param name the name of the property
     * @param value the value to set
     * @return true
     */
    public boolean setProperty(String name, String value)
    {
        // TODO: don't allow overwriting of system properties
        properties.put(name, value);
        return true;
    }

    /**
     * Add a name value pair to the project property set. It is <i>not</i> replaced it is already
     * in the set of properties.
     * 
     * @param name the name of the property
     * @param value the value to set
     * @return true if the property was not already set
     */
    public boolean addProperty(String name, String value)
    {
        String old = properties.getProperty(name);
        if (old == null)
        {
            properties.put(name, value);
            return true;
        }
        return false;
    }

    /**
     * Add jar content to the installation.
     * @param content 
     */
    public void addJarContent(URL content)
    {
        packager.addJarContent(content);
    }
    /**
     * Add jar content to the installation.
     * @param content 
     */
    public void addJarContent(URL content, List files)
    {
        packager.addJarContent(content, files);
    }
    /**
     * Add a custom jar to the installation.
     * @param ca
     * @param url
     */
    public void addCustomJar(CustomData ca, URL url)
    {
        packager.addCustomJar(ca, url);
    }
    /**
     * Add a lang pack to the installation.
     * @param iso3
     * @param iso3xmlURL
     * @param iso3FlagURL
     */
    public void addLangPack(String iso3, URL iso3xmlURL, URL iso3FlagURL)
    {
        packager.addLangPack(iso3, iso3xmlURL, iso3FlagURL);
    }
    /**
     * Add a native library to the installation.
     * @param name
     * @param url
     * @throws Exception
     */
    public void addNativeLibrary(String name, URL url) throws Exception
    {
        packager.addNativeLibrary(name, url);
    }
    /**
     * Add an unistaller library.
     * @param data
     */
    public void addNativeUninstallerLibrary(CustomData data)
    {
        packager.addNativeUninstallerLibrary(data);
    }
    /**
     * Add a pack to the installation.
     * @param pack
     */
    public void addPack(PackInfo pack)
    {
        packager.addPack(pack);
    }
    /**
     * Add a panel jar to the installation.
     * @param panel
     * @param url
     */
    public void addPanelJar(Panel panel, URL url)
    {
        packager.addPanelJar(panel, url);        
    }
    /**
     * Add a resource to the installation.
     * @param name
     * @param url
     */
    public void addResource(String name, URL url)
    {
        packager.addResource(name, url);
    }

    /**
     * Checks whether the dependencies stated in the configuration file are correct. Specifically it
     * checks that no pack point to a non existent pack and also that there are no circular
     * dependencies in the packs.
     */
    public void checkDependencies() throws CompilerException
    {
        checkDependencies(packager.getPacksList());
    }
    /**
     * Checks whether the dependencies among the given Packs. Specifically it
     * checks that no pack point to a non existent pack and also that there are no circular
     * dependencies in the packs.
     * @param packs - List<Pack> representing the packs in the installation
     */
    public void checkDependencies(List packs) throws CompilerException
    {
        // Because we use package names in the configuration file we assosiate
        // the names with the objects
        Map names = new HashMap();
        for (int i = 0; i < packs.size(); i++)
        {
            PackInfo pack = (PackInfo) packs.get(i);
            names.put(pack.getPack().name, pack);
        }
        int result = dfs(packs, names);
        // @todo More informative messages to include the source of the error
        if (result == -2)
            parseError("Circular dependency detected");
        else if (result == -1) parseError("A dependency doesn't exist");
    }

    /**
     * We use the dfs graph search algorithm to check whether the graph is acyclic as described in:
     * Thomas H. Cormen, Charles Leiserson, Ronald Rivest and Clifford Stein. Introduction to
     * algorithms 2nd Edition 540-549,MIT Press, 2001
     * 
     * @param packs The graph
     * @param names The name map
     */
    private int dfs(List packs, Map names)
    {
        Map edges = new HashMap();
        for (int i = 0; i < packs.size(); i++)
        {
            PackInfo pack = (PackInfo) packs.get(i);
            if (pack.colour == PackInfo.WHITE)
            {
                if (dfsVisit(pack, names, edges) != 0) return -1;
            }

        }
        return checkBackEdges(edges);
    }

    /**
     * This function checks for the existence of back edges.
     */
    private int checkBackEdges(Map edges)
    {
        Set keys = edges.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();)
        {
            final Object key = iterator.next();
            int color = ((Integer) edges.get(key)).intValue();
            if (color == PackInfo.GREY) { return -2; }
        }
        return 0;

    }

    /**
     * This class is used for the classification of the edges
     */
    private class Edge
    {

        PackInfo u;

        PackInfo v;

        Edge(PackInfo u, PackInfo v)
        {
            this.u = u;
            this.v = v;
        }
    }

    private int dfsVisit(PackInfo u, Map names, Map edges)
    {
        u.colour = PackInfo.GREY;
        List deps = u.getDependencies();
        if (deps != null)
        {
            for (int i = 0; i < deps.size(); i++)
            {
                String name = (String) deps.get(i);
                PackInfo v = (PackInfo) names.get(name);
                if (v == null)
                {
                    System.out.println("Failed to find dependency: "+name);
                    return -1;
                }
                Edge edge = new Edge(u, v);
                if (edges.get(edge) == null) edges.put(edge, new Integer(v.colour));

                if (v.colour == PackInfo.WHITE)
                {

                    final int result = dfsVisit(v, names, edges);
                    if (result != 0) return result;
                }
            }
        }
        u.colour = PackInfo.BLACK;
        return 0;
    }

    /**
     * Recursive method to add files in a pack.
     * 
     * @param file The file to add.
     * @param targetdir The relative path to the parent.
     * @param osList The target OS constraints.
     * @param override Overriding behaviour.
     * @param pack Pack to be packed into
     * @param additionals Map which contains additional data
     * @exception FileNotFoundException if the file does not exist
     */
    protected void addRecursively(File file, String targetdir, List osList, int override,
            PackInfo pack, Map additionals) throws IOException
    {
        String targetfile = targetdir + "/" + file.getName();
        if (!file.isDirectory())
            pack.addFile(file, targetfile, osList, override, additionals);
        else
        {
            File[] files = file.listFiles();
            if (files.length == 0) // The directory is empty so must be added
                pack.addFile(file, targetfile, osList, override, additionals);
            else
            {
                // new targetdir = targetfile;
                for (int i = 0; i < files.length; i++)
                    addRecursively(files[i], targetfile, osList, override, pack, additionals);
            }
        }
    }

    /**
     * Look for an IzPack resource either in the compiler jar, or within IZPACK_HOME. The path must
     * not be absolute. The path must use '/' as the fileSeparator (it's used to access the jar
     * file). If the resource is not found, a CompilerException is thrown indicating fault in the
     * parent element.
     * 
     * @param path the relative path (using '/' as separator) to the resource.
     * @param desc the description of the resource used to report errors
     * @return a URL to the resource.
     */
    public URL findIzPackResource(String path, String desc)
            throws CompilerException
    {
        URL url = getClass().getResource("/" + path);
        if (url == null)
        {
            File resource = new File(path);
            if (!resource.isAbsolute()) resource = new File(IZPACK_HOME, path);

            if (!resource.exists()) // fatal
                parseError(desc + " not found: " + resource);

            try
            {
                url = resource.toURL();
            }
            catch (MalformedURLException how)
            {
                parseError(desc + "(" + resource + ")", how);
            }
        }

        return url;
    }

    /**
     * Create parse error with consistent messages. Includes file name. For use When parent is
     * unknown.
     * 
     * @param message Brief message explaining error
     */
    public void parseError(String message) throws CompilerException
    {
        this.compileFailed = true;
        throw new CompilerException(message);
    }
    public void parseError(String message, Throwable how) throws CompilerException
    {
        this.compileFailed = true;
        throw new CompilerException(message, how);
    }

    /**
     * The main method if the compiler is invoked by a command-line call.
     * This simply calls the CompilerConfig.main method.
     * 
     * @param args The arguments passed on the command-line.
     */
    public static void main(String[] args)
    {
        CompilerConfig.main(args);
    }

    // -------------------------------------------------------------------------
    // ------------- Listener stuff ------------------------- START ------------

    /**
     * This method parses install.xml for defined listeners and put them in the right position. If
     * posible, the listeners will be validated. Listener declaration is a fragmention in
     * install.xml like : &lt;listeners&gt; &lt;listener compiler="PermissionCompilerListener"
     * installer="PermissionInstallerListener"/1gt; &lt;/listeners&gt;
     * 
     * @param type The listener type.
     * @param className The class name.
     * @param jarPath The jar path.
     * @param constraints The list of constraints.
     * @throws Exception Thrown in case an error occurs.
     */
    public void addCustomListener(int type, String className, String jarPath, List constraints) throws Exception
    {
        jarPath = replaceProperties(jarPath);
        URL url = findIzPackResource(jarPath, "CustomAction jar file");
        List filePaths = getContainedFilePaths(url);
        String fullClassName = getFullClassName(url, className);
        CustomData ca = new CustomData(fullClassName, filePaths, constraints, type);
        packager.addCustomJar(ca, url);
    }

    /**
     * Returns a list which contains the pathes of all files which are included in the given url.
     * This method expects as the url param a jar.
     * 
     * @param url url of the jar file
     * @return full qualified paths of the contained files
     * @throws Exception
     */
    private List getContainedFilePaths(URL url) throws Exception
    {
        JarInputStream jis = new JarInputStream(url.openStream());
        ZipEntry zentry = null;
        ArrayList fullNames = new ArrayList();
        while ((zentry = jis.getNextEntry()) != null)
        {
            String name = zentry.getName();
            // Add only files, no directory entries.
            if (!zentry.isDirectory()) fullNames.add(name);
        }
        jis.close();
        return (fullNames);
    }

    /**
     * Returns the qualified class name for the given class. This method expects as the url param a
     * jar file which contains the given class. It scans the zip entries of the jar file.
     * 
     * @param url url of the jar file which contains the class
     * @param className short name of the class for which the full name should be resolved
     * @return full qualified class name
     * @throws Exception
     */
    private String getFullClassName(URL url, String className) throws Exception
    {
        JarInputStream jis = new JarInputStream(url.openStream());
        ZipEntry zentry = null;
        while ((zentry = jis.getNextEntry()) != null)
        {
            String name = zentry.getName();
            int lastPos = name.lastIndexOf(".class");
            if (lastPos < 0)
            {
                continue; // No class file.
            }
            name = name.replace('/', '.');
            int pos = -1;
            if (className != null)
            {
                pos = name.indexOf(className);
            }
            if (name.length() == pos + className.length() + 6) // "Main" class
            // found
            {
                jis.close();
                return (name.substring(0, lastPos));
            }
        }
        jis.close();
        return (null);
    }

    // -------------------------------------------------------------------------
    // ------------- Listener stuff ------------------------- END ------------

    /**
     * Used to handle the packager messages in the command-line mode.
     * 
     * @author julien created October 26, 2002
     */
    static class CmdlinePackagerListener implements PackagerListener
    {

        /**
         * Print a message to the console at default priority (MSG_INFO).
         * 
         * @param info The information.
         */
        public void packagerMsg(String info)
        {
            packagerMsg(info, MSG_INFO);
        }

        /**
         * Print a message to the console at the specified priority.
         * 
         * @param info The information.
         */
        public void packagerMsg(String info, int priority)
        {
            final String prefix;
            switch (priority)
            {
            case MSG_DEBUG:
                prefix = "[ DEBUG ] ";
                break;
            case MSG_ERR:
                prefix = "[ ERROR ] ";
                break;
            case MSG_WARN:
                prefix = "[ WARNING ] ";
                break;
            case MSG_INFO:
            case MSG_VERBOSE:
            default: // don't die, but don't prepend anything
                prefix = "";
            }

            System.out.println(prefix + info);
        }

        /** Called when the packager starts. */
        public void packagerStart()
        {
            System.out.println("[ Begin ]");
            System.out.println();
        }

        /** Called when the packager stops. */
        public void packagerStop()
        {
            System.out.println();
            System.out.println("[ End ]");
        }
    }

}
