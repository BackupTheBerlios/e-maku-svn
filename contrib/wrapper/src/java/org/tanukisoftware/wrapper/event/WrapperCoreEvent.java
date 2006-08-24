package org.tanukisoftware.wrapper.event;

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

// $Log: WrapperCoreEvent.java,v $
// Revision 1.4  2006/02/24 05:45:57  mortenson
// Update the copyright.
//
// Revision 1.3  2005/05/23 02:39:58  mortenson
// Update the copyright information.
//
// Revision 1.2  2004/11/29 13:15:38  mortenson
// Fix some javadocs problems.
//
// Revision 1.1  2004/11/22 04:06:43  mortenson
// Add an event model to make it possible to communicate with user applications in
// a more flexible way.
//

/**
 * WrapperCoreEvents are used to notify the listener of the internal
 *  workings of the Wrapper.
 *
 * WARNING - Great care should be taken when receiving events of this type.
 *  They are sent from within the Wrapper's internal timing thread.  If the
 *  listner takes too much time working with the event, Wrapper performance
 *  could be adversely affected.  If unsure, it is recommended that events
 *  of this type not be included.
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 * @version $Revision: 1.4 $
 */
public abstract class WrapperCoreEvent
    extends WrapperEvent
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new WrapperCoreEvent.
     */
    protected WrapperCoreEvent()
    {
    }
    
    /*---------------------------------------------------------------
     * WrapperCoreEvent Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a set of event flags for which the event should be fired.
     *  This value is compared with the mask supplied when when a
     *  WrapperEventListener is registered to decide which listeners should
     *  receive the event.
     * <p>
     * If a subclassed, the return value of the super class should usually
     *  be ORed with any additional flags.
     *
     * @return a set of event flags.
     */
    public long getFlags()
    {
        return super.getFlags() | WrapperEventListener.EVENT_FLAG_CORE;
    }
}
