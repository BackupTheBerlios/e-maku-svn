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
 */

// $Log: WrapperServiceException.java,v $
// Revision 1.3  2006/02/24 05:45:57  mortenson
// Update the copyright.
//
// Revision 1.2  2005/05/23 02:41:12  mortenson
// Update the copyright information.
//
// Revision 1.1  2004/11/22 09:35:48  mortenson
// Add methods for controlling other services.
//

/**
 * WrapperServiceExceptions are thrown when the Wrapper is unable to obtain
 *  information on a requested service.
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 * @version $Revision: 1.3 $
 */
public class WrapperServiceException
    extends Exception
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new WrapperServiceException.
     *
     * @param message Message describing the exception.
     */
    WrapperServiceException( byte[] message )
    {
        super( new String( message ) );
    }
}

