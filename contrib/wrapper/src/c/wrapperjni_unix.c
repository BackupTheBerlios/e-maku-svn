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
 * $Log: wrapperjni_unix.c,v $
 * Revision 1.21  2006/02/24 05:43:36  mortenson
 * Update the copyright.
 *
 * Revision 1.20  2005/05/23 02:37:55  mortenson
 * Update the copyright information.
 *
 * Revision 1.19  2004/12/16 14:13:45  mortenson
 * Fix a problem where TERM signals were not being correctly ignored by the JVM
 * process on UNIX platforms even if wrapper.ignore_signals was set.
 *
 * Revision 1.18  2004/12/08 05:08:49  mortenson
 * Correct the return types of the unsupported Windows service related methods.
 *
 * Revision 1.17  2004/11/22 09:35:47  mortenson
 * Add methods for controlling other services.
 *
 * Revision 1.16  2004/11/15 08:15:49  mortenson
 * Make it possible for users to access the Wrapper and JVM PIDs from within the JVM.
 *
 * Revision 1.15  2004/01/16 04:42:00  mortenson
 * The license was revised for this version to include a copyright omission.
 * This change is to be retroactively applied to all versions of the Java
 * Service Wrapper starting with version 3.0.0.
 *
 * Revision 1.14  2004/01/12 17:40:03  mortenson
 * Fix some compiler warnings on Solaris
 *
 * Revision 1.13  2004/01/10 19:57:48  mortenson
 * Add the ability to request a user's groups on UNIX platforms.
 *
 * Revision 1.12  2004/01/10 18:40:16  mortenson
 * Add additional user info to the UNIX user object.
 *
 * Revision 1.11  2004/01/10 17:17:26  mortenson
 * Add the ability to request user information.
 *
 * Revision 1.10  2003/11/03 10:27:46  mortenson
 * Fix some link errors.
 *
 * Revision 1.9  2003/11/02 20:55:54  mortenson
 * Implement stubs for the user methods.
 *
 * Revision 1.8  2003/10/31 11:10:46  mortenson
 * Add a getLastErrorText function so we can display more user friendly messages
 * within the native library.
 *
 * Revision 1.7  2003/10/31 05:59:34  mortenson
 * Added a new method, setConsoleTitle, to the WrapperManager class which
 * enables the application to dynamically set the console title.
 *
 * Revision 1.6  2003/04/03 04:05:22  mortenson
 * Fix several typos in the docs.  Thanks to Mike Castle.
 *
 * Revision 1.5  2003/02/03 06:55:27  mortenson
 * License transfer to TanukiSoftware.org
 *
 */

#ifndef WIN32

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <grp.h>
#include <pwd.h>
#include <signal.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include "wrapperjni.h"

static pid_t wrapperProcessId = -1;

/**
 * Handle interrupt signals (i.e. Crtl-C).
 */
void handleInterrupt(int sig_num) {
    signal(SIGINT, handleInterrupt);
    wrapperJNIHandleSignal(org_tanukisoftware_wrapper_WrapperManager_WRAPPER_CTRL_C_EVENT);
}

/**
 * Handle termination signals (i.e. machine is shutting down).
 */
void handleTermination(int sig_num) {
    signal(SIGTERM, handleTermination); 
    wrapperJNIHandleSignal(org_tanukisoftware_wrapper_WrapperManager_WRAPPER_CTRL_TERM_EVENT);
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeInit
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeInit(JNIEnv *env, jclass clazz, jboolean debugging) {
    wrapperJNIDebugging = debugging;

    if (wrapperJNIDebugging) {
        /* This is useful for making sure that the JNI call is working. */
        printf("Inside native WrapperManager initialization method\n");
        fflush(NULL);
    }

    /* Set handlers for signals */
    signal(SIGINT,  handleInterrupt);
    signal(SIGTERM, handleTermination);

    /* Store the current process Id */
    wrapperProcessId = getpid();
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeGetJavaPID
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeGetJavaPID(JNIEnv *env, jclass clazz) {
    return (int)getpid();
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeRequestThreadGroup
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeRequestThreadDump(JNIEnv *env, jclass clazz) {
    if (wrapperJNIDebugging) {
        printf("Sending SIGQUIT event to process group %d.\n", (int)wrapperProcessId);
        fflush(NULL);
    }
    if (kill(wrapperProcessId, SIGQUIT) < 0) {
        printf("Unable to send SIGQUIT to JVM process: %s\n", getLastErrorText());
        fflush(NULL);
    }
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeSetConsoleTitle
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeSetConsoleTitle(JNIEnv *env, jclass clazz, jbyteArray jTitleBytes) {
    if (wrapperJNIDebugging) {
        printf("Setting the console title not supported on UNIX platforms.\n");
        fflush(NULL);
    }
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeGetUser
 * Signature: (Z)Lorg/tanukisoftware/wrapper/WrapperUser;
 */
/*#define UVERBOSE*/
JNIEXPORT jobject JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeGetUser(JNIEnv *env, jclass clazz, jboolean groups) {
    jclass wrapperUserClass;
    jmethodID constructor;
    jmethodID setGroup;
    jmethodID addGroup;
    uid_t uid;
    struct passwd *pw;
    gid_t ugid;
    jbyteArray jUser;
    jbyteArray jRealName;
    jbyteArray jHome;
    jbyteArray jShell;
    jobject wrapperUser = NULL;
    struct group *aGroup;
    int member;
    int i;
    gid_t ggid;
    jbyteArray jGroupName;

    /* Look for the WrapperUser class. Ignore failures as JNI throws an exception. */
    if ((wrapperUserClass = (*env)->FindClass(env, "org/tanukisoftware/wrapper/WrapperUNIXUser")) != NULL) {

        /* Look for the constructor. Ignore failures. */
        if ((constructor = (*env)->GetMethodID(env, wrapperUserClass, "<init>", "(II[B[B[B[B)V")) != NULL) {

            uid = geteuid();
            pw = getpwuid(uid);
            ugid = pw->pw_gid;

            /* Create the arguments to the constructor as java objects */

            /* User byte array */
            jUser = (*env)->NewByteArray(env, strlen(pw->pw_name));
            (*env)->SetByteArrayRegion(env, jUser, 0, strlen(pw->pw_name), (jbyte*)pw->pw_name);

            /* Real Name byte array */
            jRealName = (*env)->NewByteArray(env, strlen(pw->pw_gecos));
            (*env)->SetByteArrayRegion(env, jRealName, 0, strlen(pw->pw_gecos), (jbyte*)pw->pw_gecos);

            /* Home byte array */
            jHome = (*env)->NewByteArray(env, strlen(pw->pw_dir));
            (*env)->SetByteArrayRegion(env, jHome, 0, strlen(pw->pw_dir), (jbyte*)pw->pw_dir);

            /* Shell byte array */
            jShell = (*env)->NewByteArray(env, strlen(pw->pw_shell));
            (*env)->SetByteArrayRegion(env, jShell, 0, strlen(pw->pw_shell), (jbyte*)pw->pw_shell);

            /* Now create the new wrapperUser using the constructor arguments collected above. */
            wrapperUser = (*env)->NewObject(env, wrapperUserClass, constructor, uid, ugid, jUser, jRealName, jHome, jShell);

            /* If the caller requested the user's groups then look them up. */
            if (groups) {
               /* Set the user group. */
               if ((setGroup = (*env)->GetMethodID(env, wrapperUserClass, "setGroup", "(I[B)V")) != NULL) {
                   if ((aGroup = getgrgid(ugid)) != NULL) {
                       ggid = aGroup->gr_gid;

                       /* Group name byte array */
                       jGroupName = (*env)->NewByteArray(env, strlen(aGroup->gr_name));
                       (*env)->SetByteArrayRegion(env, jGroupName, 0, strlen(aGroup->gr_name), (jbyte*)aGroup->gr_name);

                        /* Add the group to the user. */
                       (*env)->CallVoidMethod(env, wrapperUser, setGroup, ggid, jGroupName);
                   }
               }

               /* Look for the addGroup method. Ignore failures. */
               if ((addGroup = (*env)->GetMethodID(env, wrapperUserClass, "addGroup", "(I[B)V")) != NULL) {

                   setgrent();
                   while ((aGroup = getgrent()) != NULL) {
                       /* Search the member list to decide whether or not the user is a member. */
                       member = 0;
                       i = 0;
                       while ((member == 0) && aGroup->gr_mem[i]) {
                           if (strcmp(aGroup->gr_mem[i], pw->pw_name) == 0) {
                               member = 1;
                           }
                           i++;
                       }

                       if (member) {
                           ggid = aGroup->gr_gid;

                           /* Group name byte array */
                           jGroupName = (*env)->NewByteArray(env, strlen(aGroup->gr_name));
                           (*env)->SetByteArrayRegion(env, jGroupName, 0, strlen(aGroup->gr_name), (jbyte*)aGroup->gr_name);

                           /* Add the group to the user. */
                           (*env)->CallVoidMethod(env, wrapperUser, addGroup, ggid, jGroupName);
                       }
                   }
                   endgrent();
                }
            }
        }
    }

    return wrapperUser;
}


/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeGetInteractiveUser
 * Signature: (Z)Lorg/tanukisoftware/wrapper/WrapperUser;
 */
JNIEXPORT jobject JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeGetInteractiveUser(JNIEnv *env, jclass clazz, jboolean groups) {
    /* If the DISPLAY environment variable is set then assume that this user
     *  has access to an X display, in which case we will return the same thing
     *  as nativeGetUser. */
    if (getenv("DISPLAY")) {
        /* This is an interactive JVM since it has access to a display. */
        return Java_org_tanukisoftware_wrapper_WrapperManager_nativeGetUser(env, clazz, groups);
    } else {
        /* There is no DISPLAY variable, so assume that this JVM is non-interactive. */
        return NULL;
    }
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeListServices
 * Signature: ()[Lorg/tanukisoftware/wrapper/WrapperWin32Service;
 */
JNIEXPORT jobjectArray JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeListServices(JNIEnv *env, jclass clazz) {
    /** Not supported on UNIX platforms. */
    return NULL;
}

/*
 * Class:     org_tanukisoftware_wrapper_WrapperManager
 * Method:    nativeSendServiceControlCode
 * Signature: ([BI)Lorg/tanukisoftware/wrapper/WrapperWin32Service;
 */
JNIEXPORT jobject JNICALL
Java_org_tanukisoftware_wrapper_WrapperManager_nativeSendServiceControlCode(JNIEnv *env, jclass clazz, jbyteArray serviceName, jint controlCode) {
    /** Not supported on UNIX platforms. */
    return NULL;
}

#endif
