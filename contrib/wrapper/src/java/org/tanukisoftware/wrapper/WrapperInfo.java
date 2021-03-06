package org.tanukisoftware.wrapper;

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

// $Log: WrapperInfo.java.in,v $
// Revision 1.8  2006/02/24 05:45:57  mortenson
// Update the copyright.
//
// Revision 1.7  2005/05/23 02:41:12  mortenson
// Update the copyright information.
//
// Revision 1.6  2004/11/19 03:38:24  mortenson
// Add a private constructor so that the WrapperInfo class can not be instantiated.
//
// Revision 1.5  2004/06/30 09:02:33  mortenson
// Remove unused imports.
//
// Revision 1.4  2004/01/16 04:42:00  mortenson
// The license was revised for this version to include a copyright omission.
// This change is to be retroactively applied to all versions of the Java
// Service Wrapper starting with version 3.0.0.
//
// Revision 1.3  2003/05/29 10:11:21  mortenson
// Fix a minor problem where the hour in the date returned by
// WrapperInfo.getBuildTime() was not base 24.
//
// Revision 1.2  2003/04/03 04:05:23  mortenson
// Fix several typos in the docs.  Thanks to Mike Castle.
//
// Revision 1.1  2003/02/03 06:55:28  mortenson
// License transfer to TanukiSoftware.org
//

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * WrapperInfo.java is build as part of the build process and should not be
 *  modified.  Any changes to this class should be made to WrapperInfo.java.in
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 * @version $Revision: 1.8 $
 */
final class WrapperInfo
{
    /** Version of the Wrapper. */
    private static final String   m_version         = "3.2.1";

    /** Date that the Wrapper was built. */
    private static final Calendar m_build           = Calendar.getInstance();
    
    /** Static initializer to create the build calendar from info hardcoded
     *   during the build. */
    static
    {
        Calendar buildDate = Calendar.getInstance();
        Calendar buildTime = Calendar.getInstance();
        try
        {
            buildDate.setTime( new SimpleDateFormat( "yyyyMMdd" ).parse( "20060824" ) );
            buildTime.setTime( new SimpleDateFormat( "HHmm" ).parse( "1612" ) );
            
            m_build.set( buildDate.get( Calendar.YEAR ), 
                        buildDate.get( Calendar.MONTH ), 
                        buildDate.get( Calendar.DATE ),
                        buildTime.get( Calendar.HOUR_OF_DAY ),
                        buildTime.get( Calendar.MINUTE ) );

        }
        catch ( ParseException e )
        {
            System.out.println( "Can not parse build date: " + e.getMessage() );
        }
    }

    /**
     * Returns the version of the Wrapper.
     *
     * @return the version of the Wrapper.
     */
    static String getVersion()
    {
        return m_version;
    }
    
    /**
     * Returns the time that the Wrapper was built.
     *
     * @return The time that the Wrapper was built.
     */
    static String getBuildTime()
    {
        DateFormat df = new SimpleDateFormat( "HH:mm zz MMM d, yyyy" ); 
        return df.format( m_build.getTime() );
    }
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Can not be instantiated.
     */
    private WrapperInfo()
    {
    }
}

