package org.tanukisoftware.wrapper.jmx;

/*
 * Copyright (c) 1999, 2004 Tanuki Software
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of the Java Service Wrapper and associated
 * documentation files (the "Software"), to deal in the Software
 * without  restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sub-license,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// $Log: WrapperManagerTestingMBean.java,v $
// Revision 1.2  2004/01/16 04:42:01  mortenson
// The license was revised for this version to include a copyright omission.
// This change is to be retroactively applied to all versions of the Java
// Service Wrapper starting with version 3.0.0.
//
// Revision 1.1  2003/09/03 14:39:58  mortenson
// Added a pair of MBean interfaces which allow the Wrapper to be controlled
// using JMX.  See the new JMX section in the documentation for details.
//

/**
 * This MBean interface provides access to a number of actions which can be
 *  useful for testing how well an application responds to JVM crashes.  It
 *  has been broken out frtom the WrapperManagerMBean interface so system
 *  administrators can easily disable the testing functions.
 */
public interface WrapperManagerTestingMBean
{
    /**
     * Causes the WrapperManager to go into a state which makes the JVM appear
     *  to be hung when viewed from the native Wrapper code.  Does not have
     *  any effect when the JVM is not being controlled from the native
     *  Wrapper.
     */
    void appearHung();
    
    /**
     * Cause an access violation within native JNI code.  This currently causes
     *  the access violation by attempting to write to a null pointer.
     */
    void accessViolationNative();
    
    /**
     * Tells the native wrapper that the JVM wants to shut down and then
     *  promptly halts.  Be careful when using this method as an application
     *  will not be given a chance to shutdown cleanly.
     *
     * @param exitCode The exit code that the Wrapper will return when it exits.
     */
    void stopImmediate( int exitCode );
}
