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
 * 
 *
 * $Log: wrapperjni.h,v $
 * Revision 1.8  2006/02/24 05:43:36  mortenson
 * Update the copyright.
 *
 * Revision 1.7  2005/05/23 02:37:55  mortenson
 * Update the copyright information.
 *
 * Revision 1.6  2004/01/16 04:42:00  mortenson
 * The license was revised for this version to include a copyright omission.
 * This change is to be retroactively applied to all versions of the Java
 * Service Wrapper starting with version 3.0.0.
 *
 * Revision 1.5  2003/10/31 11:10:46  mortenson
 * Add a getLastErrorText function so we can display more user friendly messages
 * within the native library.
 *
 * Revision 1.4  2003/04/03 04:05:22  mortenson
 * Fix several typos in the docs.  Thanks to Mike Castle.
 *
 * Revision 1.3  2003/02/03 06:55:27  mortenson
 * License transfer to TanukiSoftware.org
 *
 */

#ifndef _WRAPPERJNI_H
#define _WRAPPERJNI_H

#include "org_tanukisoftware_wrapper_WrapperManager.h"

extern char* getLastErrorText();
extern int wrapperJNIDebugging;
extern void wrapperJNIHandleSignal(int signal);

#endif
