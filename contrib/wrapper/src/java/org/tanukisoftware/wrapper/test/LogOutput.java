package org.tanukisoftware.wrapper.test;

/*
 * Copyright (c) 1999, 2006 Tanuki Software Inc.
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
 * 
 * 
 * Portions of the Software have been derived from source code
 * developed by Silver Egg Technology under the following license:
 * 
 * Copyright (c) 2001 Silver Egg Technology
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without 
 * restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sub-license, and/or 
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 */

// $Log: LogOutput.java,v $
// Revision 1.7  2006/02/24 05:45:59  mortenson
// Update the copyright.
//
// Revision 1.6  2005/05/23 02:39:30  mortenson
// Update the copyright information.
//
// Revision 1.5  2004/01/16 04:41:55  mortenson
// The license was revised for this version to include a copyright omission.
// This change is to be retroactively applied to all versions of the Java
// Service Wrapper starting with version 3.0.0.
//
// Revision 1.4  2003/04/16 00:11:07  mortenson
// Back out my last commit.  I was being silly.  The test was not valid as it was not
// the first output from the JVM.  Moved the test to LoadedWrapperListener.java where
// I could actually write an initial line feed.
//
// Revision 1.3  2003/04/15 23:52:46  mortenson
// Add a test for an initial line feed.
//
// Revision 1.2  2003/04/03 04:05:22  mortenson
// Fix several typos in the docs.  Thanks to Mike Castle.
//
// Revision 1.1  2003/02/03 06:55:29  mortenson
// License transfer to TanukiSoftware.org
//

import org.tanukisoftware.wrapper.WrapperManager;

/**
 *
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 * @version $Revision: 1.7 $
 */
public class LogOutput {
    private static void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }
    
    /*---------------------------------------------------------------
     * Main Method
     *-------------------------------------------------------------*/
    public static void main(String[] args) {
        System.out.println("Test the various log levels...");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_DEBUG,  "Debug output");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO,   "Info output");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_STATUS, "Status output");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_WARN,   "Warn output");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_ERROR,  "Error output");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_FATAL,  "Fatal output");
        
        // Let things catch up as the timing of WrapperManager.log output and System.out
        //  output can not be guaranteed.
        sleep();
        
        System.out.println("Put the logger through its paces...");
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO,  "Special C characters in %s %d % %%");
        sleep();
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO,   "");
        sleep();
        
        String sa = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            sb.append(sa);
        }
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO, sb.toString());
        sleep();

        sb = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            sb.append(sa);
            sb.append("\n");
        }
        WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO, sb.toString());
        sleep();
        
        for (int i = 0; i < 100; i++) {
            WrapperManager.log(WrapperManager.WRAPPER_LOG_LEVEL_INFO, sa);
        }
    }
}

