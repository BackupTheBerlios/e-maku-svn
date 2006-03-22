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
 *
 * $Log: wrapperinfo.c.in,v $
 * Revision 1.2  2004/01/16 04:42:00  mortenson
 * The license was revised for this version to include a copyright omission.
 * This change is to be retroactively applied to all versions of the Java
 * Service Wrapper starting with version 3.0.0.
 *
 * Revision 1.1  2003/11/05 16:45:42  mortenson
 * The WrapperManager class now checks to make sure that its current version
 * matches the version of the native library and Wrapper.
 *
 */

#include "wrapperinfo.h"

/**
 * wrapperinfo.c is built as part of the build process.  Ant creates this
 *  file by making a copy of wrapperinfo.c.in, replacing tokens as it does
 *  so.  If you need to make modifications to this file, the changes should
 *  always be made to wrapperinfo.c.in.
 */

char *wrapperVersion = "3.1.2";
