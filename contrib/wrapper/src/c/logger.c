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
 * $Log: logger.c,v $
 * Revision 1.70  2006/06/28 07:54:48  mortenson
 * Start using a common form of strcmp to make unix and windows code as
 * replicable as possible.
 *
 * Revision 1.69  2006/06/27 06:21:28  mortenson
 * Fix some compiler problems caused by the Facility patch.
 *
 * Revision 1.68  2006/06/27 06:04:59  mortenson
 * Add a new wrapper.syslog.facility property which makes it possible to specify the
 * syslog facility on UNIX systems.
 *
 * Revision 1.67  2006/02/28 16:01:46  mortenson
 * Add support for MacOSX Universal Binary distributions.
 *
 * Revision 1.66  2006/02/27 02:51:38  mortenson
 * Fix a problem where some of the new log wrapping features were not working
 * correctly if the directory or current wrapper log file did not exist.
 *
 * Revision 1.65  2006/02/24 05:43:36  mortenson
 * Update the copyright.
 *
 * Revision 1.64  2006/02/10 14:40:48  mortenson
 * Fix a linux compiler error.
 *
 * Revision 1.63  2006/02/10 14:27:10  mortenson
 * Added a new wrapper.console.flush property which forces the wrapper to
 * explicitly flush stdout after each line of log output.
 *
 * Revision 1.62  2006/02/03 05:37:29  mortenson
 * Fix a potential crash error if the format of any log target is blank.
 *
 * Revision 1.61  2006/02/02 06:38:07  mortenson
 * Fix a compiler warning when debug is enabled.
 *
 * Revision 1.60  2006/01/12 05:24:15  mortenson
 * Fix comments.
 *
 * Revision 1.59  2006/01/12 04:58:30  mortenson
 * Fix a compile problem with the glob api.
 *
 * Revision 1.58  2006/01/12 04:48:40  mortenson
 * Implement max file count checking for DATE rolled files.
 *
 * Revision 1.57  2006/01/11 16:13:11  mortenson
 * Add support for log file roll modes.
 *
 * Revision 1.56  2006/01/11 06:55:15  mortenson
 * Go through and clean up unwanted type casts from const to normal strings.
 * Start on the logfile roll mode feature.
 *
 * Revision 1.55  2005/11/07 07:23:36  mortenson
 * The cmask should have been in octal.
 *
 * Revision 1.54  2005/11/07 07:04:52  mortenson
 * Make it possible to configure the umask for all files created by the Wrapper and
 * that of the JVM.
 *
 * Revision 1.53  2005/05/23 02:37:54  mortenson
 * Update the copyright information.
 *
 * Revision 1.52  2005/05/02 15:12:02  mortenson
 * Fix a problem where a file not found error was showing up in the logs if the Wrapper
 * was started when a wrapper.log file did not yet exist. (Unreleased)
 *
 * Revision 1.51  2004/10/20 07:55:35  mortenson
 * Make sure that the logfile is flushed in a timely manner rather than leaving
 * it entirely up to the OS.
 *
 * Revision 1.50  2004/10/19 11:48:20  mortenson
 * Rework logging so that the logfile is kept open.  Results in a 4 fold speed increase.
 *
 * Revision 1.49  2004/09/22 11:09:44  mortenson
 * Remove some debug output that was added to track down a shutdown crash.
 *
 * Revision 1.48  2004/09/16 04:04:32  mortenson
 * Close the Handle to the logging mutex on shutdown.
 *
 * Revision 1.47  2004/09/06 08:01:53  mortenson
 * Correct the wrapper.logfile.maxsize property so that a a kilobyte is now 1024
 * rather than 1000, and a megabyte is a megabyte.
 *
 * Revision 1.46  2004/08/06 16:17:04  mortenson
 * Added a new wrapper.java.command.loglevel property which makes it possible
 * to control the log level of the generated java command.
 *
 * Revision 1.45  2004/07/09 17:04:57  mortenson
 * Set an icon for the Wrapper binary on Windows versions.
 *
 * Revision 1.44  2004/07/05 07:43:53  mortenson
 * Fix a deadlock on solaris by being very careful that we never perform any direct
 * logging from within a signal handler.
 *
 * Revision 1.43  2004/06/22 03:21:47  mortenson
 * Fix a problem where is was not possible disable the wrapper log file as
 * documented in the wrapper.logfile property.  Most likely broken way back
 * in version 2.2.5.
 *
 * Revision 1.42  2004/06/22 03:12:44  mortenson
 * A Windows user reported that using forward slashes in the path the log
 * file was failing.  Avoid this problem by always converting '/' to '\'
 * in the wrapper.logfile property on Windows.
 *
 * Revision 1.41  2004/06/15 06:21:55  mortenson
 * Only check if the log file needs to be rolled if we are going to be writing to the file.
 *
 * Revision 1.40  2004/06/07 03:09:31  mortenson
 * The previous commit protected log_printf with a mutex.  This removes the need to
 * have thread specific buffers for logging.  A single set of buffers is now used.  This
 * was broken out into its own commit to make it easier to back out later.
 *
 * Revision 1.39  2004/06/06 15:28:05  mortenson
 * Fix a synchronization problem in the logging code which would
 * occassionally cause the Wrapper to crash with an Access Violation.
 * The problem was only encountered when the tick timer was enabled,
 * and was only seen on multi-CPU systems.  Bug #949877.
 *
 * Revision 1.38  2004/04/16 04:13:35  mortenson
 * Fix a typo in error code that could have caused a crash if it was ever encountered.
 *
 * Revision 1.37  2004/03/20 16:55:49  mortenson
 * Add an adviser feature to help cut down on support requests from new users.
 *
 * Revision 1.36  2004/01/16 04:41:58  mortenson
 * The license was revised for this version to include a copyright omission.
 * This change is to be retroactively applied to all versions of the Java
 * Service Wrapper starting with version 3.0.0.
 *
 * Revision 1.35  2004/01/12 17:40:03  mortenson
 * Fix some compiler warnings on Solaris
 *
 * Revision 1.34  2004/01/12 17:30:32  mortenson
 * Remove a c-style comment.
 *
 * Revision 1.33  2004/01/10 18:22:00  mortenson
 * Fix a problem where the message buffer was not being expanded correctly.
 *
 * Revision 1.32  2004/01/09 19:45:03  mortenson
 * Implement the tick timer on Linux.
 *
 * Revision 1.31  2004/01/09 17:49:00  mortenson
 * Rework the logging so it is now threadsafe.
 *
 * Revision 1.30  2003/10/30 19:34:34  mortenson
 * Added a new wrapper.ntservice.console property so the console can be shown for
 * services.
 * Fixed a problem where requesting thread dumps on exit was failing when running
 * as a service.
 *
 * Revision 1.29  2003/08/02 15:50:02  mortenson
 * Implement getLastErrorText on UNIX versions.
 *
 * Revision 1.28  2003/07/04 03:57:18  mortenson
 * Convert tabs to spaces.
 *
 * Revision 1.27  2003/07/04 03:32:22  mortenson
 * The error code was being displayed twice when unable to write to the event log.
 *
 * Revision 1.26  2003/07/04 03:18:36  mortenson
 * Improve the error message displayed when the NT EventLog is full in response
 * to feature request #643617.
 *
 * Revision 1.25  2003/04/16 04:13:10  mortenson
 * Go through and clean up the computation of the number of bytes allocated in
 * malloc statements to make sure that string sizes are always multiplied by
 * sizeof(char), etc.
 *
 * Revision 1.24  2003/04/15 23:24:21  mortenson
 * Remove casts from all malloc statements.
 *
 * Revision 1.23  2003/04/15 14:17:43  mortenson
 * Clean up the code by setting all malloced variables to NULL after they are freed,
 *
 * Revision 1.22  2003/04/03 04:05:22  mortenson
 * Fix several typos in the docs.  Thanks to Mike Castle.
 *
 * Revision 1.21  2003/02/07 16:05:27  mortenson
 * Implemented feature request #676599 to enable the filtering of JVM output to
 * trigger JVM restarts or Wrapper shutdowns.
 *
 * Revision 1.20  2003/02/03 06:55:26  mortenson
 * License transfer to TanukiSoftware.org
 *
 */

/**
 * Author:
 *   Johan Sorlin   <Johan.Sorlin@Paregos.se>
 *   Leif Mortenson <leif@tanukisoftware.com>
 *
 * Version CVS $Revision: 1.70 $ $Date: 2006/06/28 07:54:48 $
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <time.h>
#include <sys/stat.h>
#include <string.h>
#include <errno.h>

#ifdef WIN32
#include <io.h>
#include <windows.h>
#include <tchar.h>
#include <conio.h>
#include "messages.h"
#else
#include <glob.h>
#include <syslog.h>
#include <strings.h>
#include <pthread.h>
#endif

#include "logger.h"

#ifndef TRUE
#define TRUE -1
#endif

#ifndef FALSE
#define FALSE 0
#endif

/* Global data for logger */

/* Initialize all log levels to unknown until they are set */
int currentConsoleLevel = LEVEL_UNKNOWN;
int currentLogfileLevel = LEVEL_UNKNOWN;
int currentLoginfoLevel = LEVEL_UNKNOWN;

#ifndef WIN32
/* Default syslog facility is LOG_USER */
int currentLogfacilityLevel = LOG_USER;
#endif

char *logFilePath;
char *currentLogFileName;
char *workLogFileName;
int logFileRollMode = ROLL_MODE_SIZE;
int logFileUmask = 0022;
char *logLevelNames[] = { "NONE  ", "DEBUG ", "INFO  ", "STATUS", "WARN  ", "ERROR ", "FATAL ", "ADVICE" };
char loginfoSourceName[ 1024 ];
int  logFileMaxSize = -1;
int  logFileMaxLogFiles = -1;

char logFileLastNowDate[9];

/* Defualt formats (Must be 4 chars) */
char consoleFormat[32];
char logfileFormat[32];

/* Flag to keep track of whether the console output should be flushed or not. */
int consoleFlush = FALSE;

/* Internal function declaration */
void sendEventlogMessage( int source_id, int level, char *szBuff );
void sendLoginfoMessage( int source_id, int level, char *szBuff );
#ifdef WIN32
void writeToConsole( char *lpszFmt, ... );
#endif
void checkAndRollLogs( );

/* Any log messages generated within signal handlers must be stored until we
 *  have left the signal handler to avoid deadlocks in the logging code.
 *  Messages are stored in a round robin buffer of log messages until
 *  maintainLogger is next called.
 * When we are inside of a signal, and thus when calling log_printf_queue,
 *  we know that it is safe to modify the queue as needed.  But it is possible
 *  that a signal could be fired while we are in maintainLogger, so case is
 *  taken to make sure that volatile changes are only made in log_printf_queue.
 */
#define QUEUE_SIZE 10
int queueWrapped = 0;
int queueWriteIndex = 0;
int queueReadIndex = 0;
char *queueMessages[QUEUE_SIZE];
int queueSourceIds[QUEUE_SIZE];
int queueLevels[QUEUE_SIZE];

/* Thread specific work buffers. */
#ifdef WIN32
DWORD threadIds[WRAPPER_THREAD_COUNT];
#else
pthread_t threadIds[WRAPPER_THREAD_COUNT];
#endif
char *threadMessageBuffer = NULL;
int threadMessageBufferSize = 0;
char *threadPrintBuffer = NULL;
int threadPrintBufferSize = 0;

/* Logger file pointer.  It is kept open under high log loads but closed whenever it has been idle. */
FILE *logfileFP = NULL;

/** Flag which controls whether or not the logfile is auto closed after each line. */
int autoCloseLogfile = 0;

/* The number of lines sent to the log file since the getLogfileActivity method was last called. */
DWORD logfileActivityCount;

/* Mutex for syncronization of the log_printf function. */
#ifdef WIN32
HANDLE log_printfMutexHandle = NULL;
#else
pthread_mutex_t log_printfMutex = PTHREAD_MUTEX_INITIALIZER;
#endif

#ifdef WIN32
HANDLE consoleStdoutHandle = NULL;
void setConsoleStdoutHandle( HANDLE stdoutHandle ) {
    consoleStdoutHandle = stdoutHandle;
}
#endif

/**
 * Replaces one token with another.  The length of the new token must be equal
 *  to or less than that of the old token.
 *
 * newToken may be null, implying "".
 */
char *replaceStringLongWithShort(char *string, const char *oldToken, const char *newToken) {
    int oldLen = strlen(oldToken);
    int newLen;
    char *in = string;
    char *out = string;
    
    if (newToken) {
        newLen = strlen(newToken);
    } else {
        newLen = 0;
    }
    
    /* Assertion check. */
    if (newLen > oldLen) {
        return string;
    }
    
    while (in[0] != '\0') {
        if (memcmp(in, oldToken, oldLen) == 0) {
            /* Found the oldToken.  Replace it with the new. */
            if (newLen > 0) {
                memcpy(out, newToken, newLen);
            }
            in += oldLen;
            out += newLen;
        }
        else
        {
            out[0] = in[0];
            in++;
            out++;
        }
    }
    out[0] = '\0';
    
    return string;
}

/**
 * Initializes the logger.  Returns 0 if the operation was successful.
 */
int initLogging() {
    int i;

#ifdef WIN32
    if (!(log_printfMutexHandle = CreateMutex(NULL, FALSE, NULL))) {
        printf("Failed to create logging mutex. %s\n", getLastErrorText());
        fflush(NULL);
        return 1;
    }
#endif
    
    logFileLastNowDate[0] = '\0';

    for ( i = 0; i < WRAPPER_THREAD_COUNT; i++ ) {
        threadIds[i] = 0;
    }

    return 0;
}

/**
 * Disposes of any logging resouces prior to shutdown.
 */
int disposeLogging() {
#ifdef WIN32
    if (log_printfMutexHandle) {
        if (!CloseHandle(log_printfMutexHandle))
        {
            printf("Unable to close Logging Mutex handle. %s\n", getLastErrorText());
            fflush(NULL);
            return 1;
        }
    }
#endif
    
    return 0;
}

/** Registers the calling thread so it can be recognized when it calls
 *  again later. */
void logRegisterThread( int thread_id ) {
#ifdef WIN32
    DWORD threadId;
    threadId = GetCurrentThreadId();
#else
    pthread_t threadId;
    threadId = pthread_self();
#endif

    if ( thread_id >= 0 && thread_id < WRAPPER_THREAD_COUNT )
    {
        threadIds[thread_id] = threadId;
    }
}

int getThreadId() {
    int i;
#ifdef WIN32
    DWORD threadId;
    threadId = GetCurrentThreadId();
#else
    pthread_t threadId;
    threadId = pthread_self();
#endif
    /* printf( "threadId=%lu\n", threadId ); */

    for ( i = 0; i < WRAPPER_THREAD_COUNT; i++ ) {
        if ( threadIds[i] == threadId ) {
            return i;
        }
    }
    
    printf( "WARNING - Encountered an unknown thread %ld in getThreadId().\n", (long int)threadId );
    return 0; /* WRAPPER_THREAD_SIGNAL */
}

int getLogfileRollModeForName( const char *logfileRollName ) {
    if (strcmpIgnoreCase(logfileRollName, "NONE") == 0) {
        return ROLL_MODE_NONE;
    } else if (strcmpIgnoreCase(logfileRollName, "SIZE") == 0) {
        return ROLL_MODE_SIZE;
    } else if (strcmpIgnoreCase(logfileRollName, "WRAPPER") == 0) {
        return ROLL_MODE_WRAPPER;
    } else if (strcmpIgnoreCase(logfileRollName, "JVM") == 0) {
        return ROLL_MODE_JVM;
    } else if (strcmpIgnoreCase(logfileRollName, "SIZE_OR_WRAPPER") == 0) {
        return ROLL_MODE_SIZE_OR_WRAPPER;
    } else if (strcmpIgnoreCase(logfileRollName, "SIZE_OR_JVM") == 0) {
        return ROLL_MODE_SIZE_OR_JVM;
    } else if (strcmpIgnoreCase(logfileRollName, "DATE") == 0) {
        return ROLL_MODE_DATE;
    } else {
        return ROLL_MODE_UNKNOWN;
    }
}

int getLogLevelForName( const char *logLevelName ) {
    if (strcmpIgnoreCase(logLevelName, "NONE") == 0) {
        return LEVEL_NONE;
    } else if (strcmpIgnoreCase(logLevelName, "ADVICE") == 0) {
        return LEVEL_ADVICE;
    } else if (strcmpIgnoreCase(logLevelName, "FATAL") == 0) {
        return LEVEL_FATAL;
    } else if (strcmpIgnoreCase(logLevelName, "ERROR") == 0) {
        return LEVEL_ERROR;
    } else if (strcmpIgnoreCase(logLevelName, "WARN") == 0) {
        return LEVEL_WARN;
    } else if (strcmpIgnoreCase(logLevelName, "STATUS") == 0) {
        return LEVEL_STATUS;
    } else if (strcmpIgnoreCase(logLevelName, "INFO") == 0) {
        return LEVEL_INFO;
    } else if (strcmpIgnoreCase(logLevelName, "DEBUG") == 0) {
        return LEVEL_DEBUG;
    } else {
        return LEVEL_UNKNOWN;
    }
}

#ifndef WIN32
int getLogFacilityForName( const char *logFacilityName ) {
    if (strcmpIgnoreCase(logFacilityName, "USER") == 0) {
      return LOG_USER;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL0") == 0) {
      return LOG_LOCAL0;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL1") == 0) {
      return LOG_LOCAL1;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL2") == 0) {
      return LOG_LOCAL2;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL3") == 0) {
      return LOG_LOCAL3;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL4") == 0) {
      return LOG_LOCAL4;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL5") == 0) {
      return LOG_LOCAL5;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL6") == 0) {
      return LOG_LOCAL6;
    } else if (strcmpIgnoreCase(logFacilityName, "LOCAL7") == 0) {
      return LOG_LOCAL7;
    } else {
      return LOG_USER;
    }
}
#endif

/* Logfile functions */

void setLogfilePath( const char *log_file_path ) {
    int len = strlen(log_file_path);
#ifdef WIN32
    char *c;
#endif
    
    if (logFilePath) {
        free(logFilePath);
        free(currentLogFileName);
        free(workLogFileName);
    }
    logFilePath = malloc(sizeof(char) * (len + 1));
    strcpy(logFilePath, log_file_path);

    currentLogFileName = malloc(sizeof(char) * (len + 10 + 1));
    currentLogFileName[0] = '\0';
    workLogFileName = malloc(sizeof(char) * (len + 10 + 1));
    workLogFileName[0] = '\0';
    
#ifdef WIN32	
    /* To avoid problems on some windows systems, the '/' characters must
     *  be replaced by '\' characters in the specified path. */
    c = (char *)logFilePath;
    while((c = strchr(c, '/')) != NULL) {
        c[0] = '\\';
    }
#endif
}

void setLogfileRollMode( int log_file_roll_mode ) {
    logFileRollMode = log_file_roll_mode;
}

int getLogfileRollMode() {
    return logFileRollMode;
}

void setLogfileUmask( int log_file_umask ) {
    logFileUmask = log_file_umask;
}

void setLogfileFormat( const char *log_file_format ) {
    if( log_file_format != NULL )
        strcpy( logfileFormat, log_file_format );
}

void setLogfileLevelInt( int log_file_level ) {
    currentLogfileLevel = log_file_level;
}

int getLogfileLevelInt() {
    return currentLogfileLevel;
}

void setLogfileLevel( const char *log_file_level ) {
    setLogfileLevelInt(getLogLevelForName(log_file_level));
}

void setLogfileMaxFileSize( const char *max_file_size ) {
    int multiple, i, newLength;
    char *tmpFileSizeBuff;
    char chr;

    if( max_file_size != NULL ) {
        /* Allocate buffer */
        if( (tmpFileSizeBuff = (char *) malloc(sizeof(char) * (strlen( max_file_size ) + 1))) == NULL )
            return;

        /* Generate multiple and remove unwanted chars */
        multiple = 1;
        newLength = 0;
        for( i=0; i<(int) strlen(max_file_size); i++ ) {
            chr = max_file_size[i];

            switch( chr ) {
                case 'k': /* Kilobytes */
                case 'K':
                    multiple = 1024;
                break;

                case 'M': /* Megabytes */
                case 'm':
                    multiple = 1048576;
                break;
            }

            if( (chr >= '0' && chr <= '9') || (chr == '-') )
                tmpFileSizeBuff[newLength++] = max_file_size[i];
        }
        tmpFileSizeBuff[newLength] = '\0';/* Crop string */

        logFileMaxSize = atoi( tmpFileSizeBuff );
        if( logFileMaxSize > 0 )
            logFileMaxSize *= multiple;

        /* Free memory */
        free( tmpFileSizeBuff );
        tmpFileSizeBuff = NULL;
    }
}

void setLogfileMaxFileSizeInt( int max_file_size ) {
    logFileMaxSize = max_file_size;
}

void setLogfileMaxLogFiles( int max_log_files ) {
    logFileMaxLogFiles = max_log_files;
}

/** Returns the number of lines of log file activity since the last call. */
DWORD getLogfileActivity() {
    DWORD logfileLines;
    
    /* Don't worry about synchronization here.  Any errors are not critical the way this is used. */
    logfileLines = logfileActivityCount;
    logfileActivityCount = 0;
    
    return logfileLines;
}

/** Obtains a lock on the logging mutex. */
int lockLoggingMutex() {
#ifdef WIN32
    switch (WaitForSingleObject(log_printfMutexHandle, INFINITE)) {
    case WAIT_ABANDONED:
        printf("Logging Mutex was abandoned.\n");
        fflush(NULL);
        return -1;
    case WAIT_FAILED:
        printf("Logging Mutex wait failed.\n");
        fflush(NULL);
        return -1;
    case WAIT_TIMEOUT:
        printf("Logging Mutex wait timed out.\n");
        fflush(NULL);
        return -1;
    default:
        /* Ok */
        break;
    }
#else
    pthread_mutex_lock(&log_printfMutex);
#endif
    
    return 0;
}

/** Releases a lock on the logging mutex. */
int releaseLoggingMutex() {
#ifdef WIN32
    if (!ReleaseMutex(log_printfMutexHandle)) {
        printf( "Failed to release logging mutex. %s\n", getLastErrorText());
        fflush(NULL);
        return -1;
    }
#else
    pthread_mutex_unlock(&log_printfMutex);
#endif
    return 0;
}

/** Closes the logfile if it is open. */
void closeLogfile() {
    /* We need to be very careful that only one thread is allowed in here
     *  at a time.  On Windows this is done using a Mutex object that is
     *  initialized in the initLogging function. */
    if (lockLoggingMutex()) {
        return;
    }
    
    if (logfileFP != NULL) {
#ifdef _DEBUG
        printf("Closing logfile by request...\n");
        fflush(NULL);
#endif
        
        fclose(logfileFP);
        logfileFP = NULL;
        currentLogFileName[0] = '\0';
    }

    /* Release the lock we have on this function so that other threads can get in. */
    if (releaseLoggingMutex()) {
        return;
    }
}

/** Sets the auto close log file flag. */
void setLogfileAutoClose(int autoClose) {
    autoCloseLogfile = autoClose;
}

/** Flushes any buffered logfile output to the disk. */
void flushLogfile() {
    /* We need to be very careful that only one thread is allowed in here
     *  at a time.  On Windows this is done using a Mutex object that is
     *  initialized in the initLogging function. */
    if (lockLoggingMutex()) {
        return;
    }

    if (logfileFP != NULL) {
#ifdef _DEBUG
        printf("Flushing logfile by request...\n");
        fflush(NULL);
#endif

        fflush(logfileFP);
    }

    /* Release the lock we have on this function so that other threads can get in. */
    if (releaseLoggingMutex()) {
        return;
    }
}

/* Console functions */
void setConsoleLogFormat( const char *console_log_format ) {
    if( console_log_format != NULL )
        strcpy( consoleFormat, console_log_format );
}

void setConsoleLogLevelInt( int console_log_level ) {
    currentConsoleLevel = console_log_level;
}

int getConsoleLogLevelInt() {
    return currentConsoleLevel;
}

void setConsoleLogLevel( const char *console_log_level ) {
    setConsoleLogLevelInt(getLogLevelForName(console_log_level));
}

void setConsoleFlush( int flush ) {
    consoleFlush = flush;
}


/* Syslog/eventlog functions */
void setSyslogLevelInt( int loginfo_level ) {
    currentLoginfoLevel = loginfo_level;
}

int getSyslogLevelInt() {
    return currentLoginfoLevel;
}

void setSyslogLevel( const char *loginfo_level ) {
    setSyslogLevelInt(getLogLevelForName(loginfo_level));
}

#ifndef WIN32
void setSyslogFacilityInt( int logfacility_level ) {
    currentLogfacilityLevel = logfacility_level;
}

void setSyslogFacility( const char *loginfo_level ) {
    setSyslogFacilityInt(getLogFacilityForName(loginfo_level));
}
#endif

void setSyslogEventSourceName( const char *event_source_name ) {
    if( event_source_name != NULL )
        strcpy( loginfoSourceName, event_source_name );
}

int getLowLogLevel() {
    int lowLogLevel = (currentLogfileLevel < currentConsoleLevel ? currentLogfileLevel : currentConsoleLevel);
    lowLogLevel =  (currentLoginfoLevel < lowLogLevel ? currentLoginfoLevel : lowLogLevel);
    return lowLogLevel;
}

/* Writes to and then returns a buffer that is reused by the current thread.
 *  It should not be released. */
char* buildPrintBuffer( int source_id, int level, struct tm *nowTM, char *format, char *message ) {
    int       i;
    int       reqSize;
    int       numColumns;
    char      *pos;
    int       currentColumn;
    int       handledFormat;
    
    /* Decide the number of columns and come up with a required length for the printBuffer. */
    reqSize = 0;
    for( i = 0, numColumns = 0; i < (int)strlen( format ); i++ ) {
        switch( format[i] ) {
        case 'P':
            reqSize += 8 + 3;
            numColumns++;
            break;

        case 'L':
            reqSize += 6 + 3;
            numColumns++;
            break;

        case 'D':
            reqSize += 7 + 3;
            numColumns++;
            break;

        case 'T':
            reqSize += 19 + 3;
            numColumns++;
            break;

        case 'M':
            reqSize += strlen( message ) + 3;
            numColumns++;
            break;
        }
    }

    /* Always add room for the null. */
    reqSize += 1;

    if ( threadPrintBuffer == NULL ) {
        threadPrintBuffer = (char *)malloc( reqSize * sizeof( char ) );
        threadPrintBufferSize = reqSize;
    } else if ( threadPrintBufferSize < reqSize ) {
        free( threadPrintBuffer );
        threadPrintBuffer = (char *)malloc( reqSize * sizeof( char ) );
        threadPrintBufferSize = reqSize;
    }

    /* Always start with a null terminated string in case there are no formats specified. */
    threadPrintBuffer[0] = '\0';
    
    /* Create a pointer to the beginning of the print buffer, it will be advanced
     *  as the formatted message is build up. */
    pos = threadPrintBuffer;

    /* We now have a buffer large enough to store the entire formatted message. */
    for( i = 0, currentColumn = 0; i < (int)strlen( format ); i++ ) {
        handledFormat = 1;

        switch( format[i] ) {
        case 'P':
            switch ( source_id ) {
            case WRAPPER_SOURCE_WRAPPER:
                pos += sprintf( pos, "wrapper " );
                break;

            case WRAPPER_SOURCE_PROTOCOL:
                pos += sprintf( pos, "wrapperp" );
                break;

            default:
                pos += sprintf( pos, "jvm %-4d", source_id );
                break;
            }
            currentColumn++;
            break;

        case 'L':
            pos += sprintf( pos, "%s", logLevelNames[ level ] );
            currentColumn++;
            break;

        case 'D':
            switch ( getThreadId() )
            {
            case WRAPPER_THREAD_SIGNAL:
                pos += sprintf( pos, "signal " );
                break;

            case WRAPPER_THREAD_MAIN:
                pos += sprintf( pos, "main   " );
                break;

            case WRAPPER_THREAD_SRVMAIN:
                pos += sprintf( pos, "srvmain" );
                break;

            case WRAPPER_THREAD_TIMER:
                pos += sprintf( pos, "timer  " );
                break;

            default:
                pos += sprintf( pos, "unknown" );
                break;
            }
            currentColumn++;
            break;

        case 'T':
            pos += sprintf( pos, "%04d/%02d/%02d %02d:%02d:%02d", 
                nowTM->tm_year + 1900, nowTM->tm_mon + 1, nowTM->tm_mday, 
                nowTM->tm_hour, nowTM->tm_min, nowTM->tm_sec );
            currentColumn++;
            break;

        case 'M':
            pos += sprintf( pos, "%s", message );
            currentColumn++;
            break;

        default:
            handledFormat = 0;
        }

        /* Add separator chars */
        if ( handledFormat && ( currentColumn != numColumns ) ) {
            pos += sprintf( pos, " | " );
        }
    }

    /* Return the print buffer to the caller. */
    return threadPrintBuffer;
}

void forceFlush(FILE *fp) {
    int lastError;
    
    fflush(fp);
    lastError = getLastError();
}

/**
 * Generates a log file name given.
 *
 * buffer - Buffer into which to sprintf the generated name.
 * template - Template from which the name is generated.
 * nowDate - Optional date used to replace any YYYYMMDD tokens.
 * rollNum - Optional roll number used to replace any ROLLNUM tokens.
 */
void generateLogFileName(char *buffer, const char *template, const char *nowDate, const char *rollNum ) {
    /* Copy the template to the buffer to get started. */
    sprintf(buffer, template);
    
    /* Handle the date token. */
    if (strstr(buffer, "YYYYMMDD")) {
        if (nowDate == NULL) {
            /* The token needs to be removed. */
            replaceStringLongWithShort(buffer, "-YYYYMMDD", NULL);
            replaceStringLongWithShort(buffer, "_YYYYMMDD", NULL);
            replaceStringLongWithShort(buffer, ".YYYYMMDD", NULL);
            replaceStringLongWithShort(buffer, "YYYYMMDD", NULL);
        } else {
            /* The token needs to be replaced. */
            replaceStringLongWithShort(buffer, "YYYYMMDD", nowDate);
        }
    }
    
    /* Handle the roll number token. */
    if (strstr(buffer, "ROLLNUM")) {
        if (rollNum == NULL ) {
            /* The token needs to be removed. */
            replaceStringLongWithShort(buffer, "-ROLLNUM", NULL);
            replaceStringLongWithShort(buffer, "_ROLLNUM", NULL);
            replaceStringLongWithShort(buffer, ".ROLLNUM", NULL);
            replaceStringLongWithShort(buffer, "ROLLNUM", NULL);
        } else {
            /* The token needs to be replaced. */
            replaceStringLongWithShort(buffer, "ROLLNUM", rollNum);
        }
    } else {
        /* The name did not contain a ROLLNUM token. */
        if (rollNum != NULL ) {
            /* Generate the name as if ".ROLLNUM" was appended to the template. */
            sprintf(buffer + strlen(buffer), ".%s", rollNum);
        }
    }
}

/* General log functions */
void log_printf( int source_id, int level, const char *lpszFmt, ... ) {
    va_list     vargs;
    int         count;
    char        *printBuffer;
    int         old_umask;
    char        nowDate[9];
    time_t      now;
    struct tm   *nowTM;

    /* We need to be very careful that only one thread is allowed in here
     *  at a time.  On Windows this is done using a Mutex object that is
     *  initialized in the initLogging function. */
    if (lockLoggingMutex()) {
        return;
    }

    /* Loop until the buffer is large enough that we are able to successfully
     *  print into it. Once the buffer has grown to the largest message size,
     *  smaller messages will pass through this code without looping. */
    do {
        if ( threadMessageBufferSize == 0 )
        {
            /* No buffer yet. Allocate one to get started. */
            threadMessageBufferSize = 100;
            threadMessageBuffer = (char*)malloc( threadMessageBufferSize * sizeof(char) );
        }

        /* Try writing to the buffer. */
        va_start( vargs, lpszFmt );
#ifdef WIN32
        count = _vsnprintf( threadMessageBuffer, threadMessageBufferSize, lpszFmt, vargs );
#else
        count = vsnprintf( threadMessageBuffer, threadMessageBufferSize, lpszFmt, vargs );
#endif
        va_end( vargs );
        /*
        printf( " vsnprintf->%d, size=%d\n", count, threadMessageBufferSize );
        fflush(NULL);
        */
        if ( ( count < 0 ) || ( count >= threadMessageBufferSize ) ) {
            /* If the count is exactly equal to the buffer size then a null char was not written.
             *  It must be larger.
             * Windows will return -1 if the buffer is too small. If the number is
             *  exact however, we still need to expand it to have room for the null.
             * UNIX will return the required size. */

            /* Free the old buffer for starters. */
            free( threadMessageBuffer );

            /* Decide on a new buffer size. */
            if ( count <= threadMessageBufferSize ) {
                threadMessageBufferSize += 100;
            } else if ( count + 1 <= threadMessageBufferSize + 100 ) {
                threadMessageBufferSize += 100;
            } else {
                threadMessageBufferSize = count + 1;
            }

            threadMessageBuffer = (char*)malloc( threadMessageBufferSize * sizeof(char) );

            /* Always set the count to -1 so we will loop again. */
            count = -1;
        }
    } while ( count < 0 );

    /* Build a timestamp */
    now = time( NULL );
    nowTM = localtime( &now );
    
    /* Console output by format */
    if( level >= currentConsoleLevel ) {
        /* Build up the printBuffer. */
        printBuffer = buildPrintBuffer( source_id, level, nowTM, consoleFormat, threadMessageBuffer );

        /* Write the print buffer to the console. */
#ifdef WIN32
        if ( consoleStdoutHandle != NULL ) {
            writeToConsole( "%s\n", printBuffer );
        } else {
#endif
            fprintf( stdout, "%s\n", printBuffer );
            if ( consoleFlush ) {
                fflush( stdout );
            }
#ifdef WIN32
        }
#endif
    }

    /* Logfile output by format */
    
    /* Log the message to the log file */
    if (level >= currentLogfileLevel) {
        /* If the log file was set to a blank value then it will not be used. */
        if ( strlen( logFilePath ) > 0 )
        {
            /* If this the roll mode is date then we need a nowDate for this log entry. */
            if (logFileRollMode & ROLL_MODE_DATE) {
                sprintf(nowDate, "%04d%02d%02d", nowTM->tm_year + 1900, nowTM->tm_mon + 1, nowTM->tm_mday );
            } else {
                nowDate[0] = '\0';
            }
            
            /* Make sure that the log file does not need to be rolled. */
            checkAndRollLogs(nowDate);
            
            /* If the file needs to be opened then do so. */
            if (logfileFP == NULL) {
                /* Generate the log file name. */
                if (logFileRollMode & ROLL_MODE_DATE) {
                    generateLogFileName(currentLogFileName, logFilePath, nowDate, NULL);
                } else {
                    generateLogFileName(currentLogFileName, logFilePath, NULL, NULL);
                }
                
                old_umask = umask( logFileUmask );
                logfileFP = fopen( currentLogFileName, "a" );
                if (logfileFP == NULL) {
                    /* The log file could not be opened.  Try the default file location. */
                    sprintf(currentLogFileName, "wrapper.log");
                    logfileFP = fopen( "wrapper.log", "a" );
                }
                umask(old_umask);
                
#ifdef _DEBUG				
                if (logfileFP != NULL) {
                    printf("Opened logfile\n");
                    fflush(NULL);
                }
#endif
            }
            
            if (logfileFP == NULL) {
                currentLogFileName[0] = '\0';
                printf("Unable to open logfile %s: %s\n", logFilePath, getLastErrorText());
                fflush(NULL);
            } else {
                /* We need to store the date the file was opened for. */
                strcpy(logFileLastNowDate, nowDate);
                
                /* Build up the printBuffer. */
                printBuffer = buildPrintBuffer( source_id, level, nowTM, logfileFormat, threadMessageBuffer );
                
                fprintf( logfileFP, "%s\n", printBuffer );
                
                /* Increment the activity counter. */
                logfileActivityCount++;
                
                /* Only close the file if autoClose is set.  Otherwise it will be closed later
                 *  after an appropriate period of inactivity. */
                if (autoCloseLogfile) {
#ifdef _DEBUG
                    printf("Closing logfile immediately...\n");
                    fflush(NULL);
#endif
                    
                    fclose(logfileFP);
                    logfileFP = NULL;
                    currentLogFileName[0] = '\0';
                }
                
                /* Leave the file open.  It will be closed later after a period of inactivity. */
            }
        }
    }

    /* Loginfo/Eventlog if levels match (not by format timecodes/status allready exists in evenlog) */
    switch ( level ) {
    case LEVEL_ADVICE:
        /* Advice level messages are special in that they never get logged to the
         *  EventLog / SysLog. */
        break;

    default:
        if( level >= currentLoginfoLevel ) {
            sendEventlogMessage( source_id, level, threadMessageBuffer );
            sendLoginfoMessage( source_id, level, threadMessageBuffer );
        }
    }

    /* Release the lock we have on this function so that other threads can get in. */
    if (releaseLoggingMutex()) {
        return;
    }
}

/* Internal functions */

/**
 * Create an error message from GetLastError() using the
 *  FormatMessage API Call...
 */
#ifdef WIN32
TCHAR lastErrBuf[1024];
char* getLastErrorText() {
    DWORD dwRet;
    LPTSTR lpszTemp = NULL;

    dwRet = FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | 
                           FORMAT_MESSAGE_FROM_SYSTEM |FORMAT_MESSAGE_ARGUMENT_ARRAY,
                           NULL,
                           GetLastError(),
                           LANG_NEUTRAL,
                           (LPTSTR)&lpszTemp,
                           0,
                           NULL);

    /* supplied buffer is not long enough */
    if (!dwRet || ((long)1023 < (long)dwRet+14)) {
        lastErrBuf[0] = TEXT('\0');
    } else {
        lpszTemp[lstrlen(lpszTemp)-2] = TEXT('\0');  /*remove cr and newline character */
        _stprintf( lastErrBuf, TEXT("%s (0x%x)"), lpszTemp, GetLastError());
    }

    if (lpszTemp) {
        GlobalFree((HGLOBAL) lpszTemp);
    }

    return lastErrBuf;
}
int getLastError() {
    return GetLastError();
}
#else
char* getLastErrorText() {
    return strerror(errno);
}
int getLastError() {
    return errno;
}
#endif

int registerSyslogMessageFile( ) {
#ifdef WIN32
    char buffer[ 1024 ];
    char regPath[ 1024 ];
    HKEY hKey;
    DWORD categoryCount, typesSupported;

    /* Get absolute path to service manager */
    if( GetModuleFileName( NULL, buffer, _MAX_PATH ) ) {
        sprintf( regPath, "SYSTEM\\CurrentControlSet\\Services\\Eventlog\\Application\\%s", loginfoSourceName );

        if( RegCreateKey( HKEY_LOCAL_MACHINE, regPath, (PHKEY) &hKey ) == ERROR_SUCCESS ) {
            RegCloseKey( hKey );

            if( RegOpenKeyEx( HKEY_LOCAL_MACHINE, regPath, 0, KEY_WRITE, (PHKEY) &hKey ) == ERROR_SUCCESS ) {
                /* Set EventMessageFile */
                if( RegSetValueEx( hKey, "EventMessageFile", (DWORD) 0, (DWORD) REG_SZ, (const unsigned char *) buffer, (strlen(buffer) + 1) ) != ERROR_SUCCESS ) {
                    RegCloseKey( hKey );
                    return -1;
                }

                /* Set CategoryMessageFile */
                if( RegSetValueEx( hKey, "CategoryMessageFile", (DWORD) 0, (DWORD) REG_SZ, (const unsigned char *) buffer, (strlen(buffer) + 1) ) != ERROR_SUCCESS ) {
                    RegCloseKey( hKey );
                    return -1;
                }

                /* Set CategoryCount */
                categoryCount = 12;
                if( RegSetValueEx( hKey, "CategoryCount", (DWORD) 0, (DWORD) REG_DWORD, (LPBYTE) &categoryCount, sizeof(DWORD) ) != ERROR_SUCCESS ) {
                    RegCloseKey( hKey );
                    return -1;
                }

                /* Set TypesSupported */
                typesSupported = 7;
                if( RegSetValueEx( hKey, "TypesSupported", (DWORD) 0, (DWORD) REG_DWORD, (LPBYTE) &typesSupported, sizeof(DWORD) ) != ERROR_SUCCESS ) {
                    RegCloseKey( hKey );
                    return -1;
                }

                RegCloseKey( hKey );
                return 0;
            }
        }
    }

    return -1; /* Failure */
#else
    return 0;
#endif
}

int unregisterSyslogMessageFile( ) {
#ifdef WIN32
    /* If we deregister this application, then the event viewer will not work when the program is not running. */
    /* Don't want to clutter up the Registry, but is there another way?  */
    char regPath[ 1024 ];

    /* Get absolute path to service manager */
    sprintf( regPath, "SYSTEM\\CurrentControlSet\\Services\\Eventlog\\Application\\%s", loginfoSourceName );

    if( RegDeleteKey( HKEY_LOCAL_MACHINE, regPath ) == ERROR_SUCCESS )
        return 0;

    return -1; /* Failure */
#else
    return 0;
#endif
}

void sendEventlogMessage( int source_id, int level, char *szBuff ) {
#ifdef WIN32
    char   header[16];
    char   **strings = (char **) malloc( 3 * sizeof( char * ) );
    WORD   eventType;
    HANDLE handle;
    WORD   eventID, categoryID;
    int    result;

    /* Build the source header */
    switch( source_id ) {
    case WRAPPER_SOURCE_WRAPPER:
        sprintf( header, "wrapper" );
    break;

    case WRAPPER_SOURCE_PROTOCOL:
        sprintf( header, "wrapperp" );
    break;

    default:
        sprintf( header, "jvm %d", source_id );
    break;
    }

    /* Build event type by level */
    switch( level ) {
    case LEVEL_ADVICE: /* Will not get in here. */
    case LEVEL_FATAL:
        eventType = EVENTLOG_ERROR_TYPE;
    break;

    case LEVEL_ERROR:
    case LEVEL_WARN:
        eventType = EVENTLOG_WARNING_TYPE;
    break;

    case LEVEL_STATUS:
    case LEVEL_INFO:
    case LEVEL_DEBUG:
        eventType = EVENTLOG_INFORMATION_TYPE;
    break;
    }

    /* Set the category id to the appropriate resource id. */
    if ( source_id == WRAPPER_SOURCE_WRAPPER ) {
        categoryID = MSG_EVENT_LOG_CATEGORY_WRAPPER;
    } else if ( source_id == WRAPPER_SOURCE_PROTOCOL ) {
        categoryID = MSG_EVENT_LOG_CATEGORY_PROTOCOL;
    } else {
        /* Source is a JVM. */
        switch ( source_id ) {
        case 1:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM1;
            break;

        case 2:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM2;
            break;

        case 3:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM3;
            break;

        case 4:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM4;
            break;

        case 5:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM5;
            break;

        case 6:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM6;
            break;

        case 7:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM7;
            break;

        case 8:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM8;
            break;

        case 9:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVM9;
            break;

        default:
            categoryID = MSG_EVENT_LOG_CATEGORY_JVMXX;
            break;
        }
    }

    /* Place event in eventlog */
    strings[0] = header;
    strings[1] = szBuff;
    strings[2] = 0;
    eventID = level;

    handle = RegisterEventSource( NULL, loginfoSourceName );
    if( !handle )
        return;

    result = ReportEvent(
        handle,                   /* handle to event log */
        eventType,                /* event type */
        categoryID,				  /* event category */
        MSG_EVENT_LOG_MESSAGE,    /* event identifier */
        NULL,                     /* user security identifier */
        2,                        /* number of strings to merge */
        0,                        /* size of binary data */
        (const char **) strings,  /* array of strings to merge */
        NULL                      /* binary data buffer */
    );
    if (result == 0) {
        /* If there are any errors accessing the event log, like it is full, then disable its output. */
        setSyslogLevelInt(LEVEL_NONE);

        /* Recurse so this error gets set in the log file and console.  The syslog
         *  output has been disabled so we will not get back here. */
        log_printf(WRAPPER_SOURCE_WRAPPER, LEVEL_ERROR, "Unable to write to the EventLog due to: %s", getLastErrorText());
        log_printf(WRAPPER_SOURCE_WRAPPER, LEVEL_ERROR, "Internally setting wrapper.syslog.loglevel=NONE to prevent further messages.");
    }

    DeregisterEventSource( handle );

    free( (void *) strings );
    strings = NULL;
#endif
}

void sendLoginfoMessage( int source_id, int level, char *szBuff ) {
#ifndef WIN32 /* If UNIX */
    int eventType;

    /* Build event type by level */
    switch( level ) {
        case LEVEL_FATAL:
            eventType = LOG_CRIT;
        break;

        case LEVEL_ERROR:
            eventType = LOG_ERR;
        break;

        case LEVEL_WARN:
        case LEVEL_STATUS:
            eventType = LOG_NOTICE;
        break;

        case LEVEL_INFO:
            eventType = LOG_INFO;
        break;

        case LEVEL_DEBUG:
            eventType = LOG_DEBUG;
        break;

        default:
            eventType = LOG_DEBUG;
    }

    openlog( loginfoSourceName, LOG_PID | LOG_NDELAY, currentLogfacilityLevel );
    syslog( eventType, "%s", szBuff );
    closelog( );
#endif
}

#ifdef WIN32
int vWriteToConsoleBufferSize = 100;
char *vWriteToConsoleBuffer = NULL;
void vWriteToConsole( char *lpszFmt, va_list vargs ) {
    int cnt;
    DWORD wrote;

    /* This should only be called if consoleStdoutHandle is set. */
    if ( consoleStdoutHandle == NULL ) {
        return;
    }

    if ( vWriteToConsoleBuffer == NULL ) {
        vWriteToConsoleBuffer = (char *)malloc( vWriteToConsoleBufferSize * sizeof(char) );
    }

    /* The only way I could figure out how to write to the console
     *  returned by AllocConsole when running as a service was to
     *  do all of this special casing and use the handle to the new
     *  console's stdout and the WriteConsole function.  If anyone
     *  puzzling over this code knows a better way of doing this
     *  let me know.
     * WriteConsole takes a fixed buffer and does not do any expansions
     *  We need to prepare the string to be displayed ahead of time.
     *  This means storing the message into a temporary buffer.  The
     *  following loop will expand the global buffer to hold the current
     *  message.  It will grow as needed to handle any arbitrarily large
     *  user message.  The buffer needs to be able to hold all available
     *  characters + a null char. */
    while ( ( cnt = _vsnprintf( vWriteToConsoleBuffer, vWriteToConsoleBufferSize - 1, lpszFmt, vargs ) ) < 0 ) {
        /* Expand the size of the buffer */
        free( vWriteToConsoleBuffer );
        vWriteToConsoleBufferSize += 100;
        vWriteToConsoleBuffer = (char *)malloc( vWriteToConsoleBufferSize * sizeof(char) );
    }

    /* We can now write the message. */
    WriteConsole(consoleStdoutHandle, vWriteToConsoleBuffer, strlen( vWriteToConsoleBuffer ), &wrote, NULL);
}
void writeToConsole( char *lpszFmt, ... ) {
    va_list		vargs;

    va_start( vargs, lpszFmt );
    vWriteToConsole( lpszFmt, vargs );
    va_end( vargs );
}
#endif

/**
 * Rolls log files using the ROLLNUM system.
 */
void rollLogs() {
    int i;
    char rollNum[11];
    struct stat fileStat;
    int result;
    
    /* If the log file is currently open, it needs to be closed. */
    if (logfileFP != NULL) {
#ifdef _DEBUG
        printf("Closing logfile so it can be rolled...\n");
        fflush(NULL);
#endif

        fclose(logfileFP);
        logfileFP = NULL;
        currentLogFileName[0] = '\0';
    }
    
#ifdef _DEBUG
    printf("Rolling log files...\n");
    fflush(NULL);
#endif
    
    /* We don't know how many log files need to be rotated yet, so look. */
    i = 0;
    do {
        i++;
        sprintf(rollNum, "%d", i);
        generateLogFileName(workLogFileName, logFilePath, NULL, rollNum);
        result = stat(workLogFileName, &fileStat);
#ifdef _DEBUG
        if (result == 0) {
            printf("Rolled log file %s exists.\n", workLogFileName);
            fflush(NULL);
        }
#endif
    } while((result == 0) && ((logFileMaxLogFiles <= 0) || (i < logFileMaxLogFiles)));
    
    /* Remove the file with the highest index if it exists */
    if (remove(workLogFileName)) {
        if (getLastError() == 2) {
            /* The file did not exist. */
        } else if (getLastError() == 3) {
            /* The path did not exist. */
        } else {
            printf("Unable to delete old log file: %s (%s)\n", workLogFileName, getLastErrorText());
            fflush(NULL);
        }
    }
    
    /* Now, starting at the highest file rename them up by one index. */
    for (; i > 1; i--) {
        strcpy(currentLogFileName, workLogFileName);
        sprintf(rollNum, "%d", i - 1);
        generateLogFileName(workLogFileName, logFilePath, NULL, rollNum);

        if (rename(workLogFileName, currentLogFileName) != 0) {
            if (errno == 13) {
                /* Don't log this as with other errors as that would cause recursion. */
                printf("Unable to rename log file %s to %s.  File is in use by another application.\n",
                    workLogFileName, currentLogFileName);
                fflush(NULL);
            } else {
                /* Don't log this as with other errors as that would cause recursion. */
                printf("Unable to rename log file %s to %s. (%s)\n",
                    workLogFileName, currentLogFileName, getLastErrorText());
                fflush(NULL);
            }
            return;
        }
#ifdef _DEBUG
        else {
            printf("Renamed %s to %s\n", workLogFileName, currentLogFileName);
            fflush(NULL);
        }
#endif
    }

    /* Rename the current file to the #1 index position */
    generateLogFileName(currentLogFileName, logFilePath, NULL, NULL);
    if (rename(currentLogFileName, workLogFileName) != 0) {
        if (getLastError() == 2) {
            /* File does not yet exist. */
        } else if (getLastError() == 3) {
            /* Path does not yet exist. */
        } else if (errno == 13) {
            /* Don't log this as with other errors as that would cause recursion. */
            printf("Unable to rename log file %s to %s.  File is in use by another application.\n",
                currentLogFileName, workLogFileName);
            fflush(NULL);
        } else {
            /* Don't log this as with other errors as that would cause recursion. */
            printf("Unable to rename log file %s to %s. (%s)\n",
                currentLogFileName, workLogFileName, getLastErrorText());
            fflush(NULL);
        }
        return;
    }
#ifdef _DEBUG
    else {
        printf("Renamed %s to %s\n", currentLogFileName, workLogFileName);
        fflush(NULL);
    }
#endif
}

void limitLogFileCountHandleFile(const char *currentFile, const char *testFile, char **latestFiles, int count) {
    int i, j;
    int result;

#ifdef _DEBUG
    printf("  limitLogFileCountHandleFile(%s, %s, latestFiles, %d)\n", currentFile, testFile, count);
    fflush(NULL);
#endif

    if (strcmp(testFile, currentFile) > 0) {
        /* Newer than the current file.  Ignore it. */
#ifdef _DEBUG
        printf("    Newer Ignore\n");
        fflush(NULL);
#endif
        return;
    }
    
    /* Decide where in the array of files the file should be located. */
    for (i = 0; i < count; i++) {
#ifdef _DEBUG
        printf("    i=%d\n", i);
        fflush(NULL);
#endif
        if (latestFiles[i] == NULL) {
#ifdef _DEBUG
            printf("    Store at index  %d\n", i);
            fflush(NULL);
#endif
            latestFiles[i] = malloc(sizeof(char) * (strlen(testFile) + 1));
            strcpy(latestFiles[i], testFile);
            return;
        } else {
            result = strcmp(latestFiles[i], testFile);
            if (result == 0) {
                /* Ignore. */
#ifdef _DEBUG
                printf("    Duplicate at index  %d\n", i);
                fflush(NULL);
#endif
                return;
            } else if (result < 0) {
                /* test file is newer than the one in the list, shift all files up in the array. */
                for (j = count - 1; j >= i; j--) {
                    if (latestFiles[j] != NULL) {
                        if (j < count - 1) {
                            /* Move the file up. */
                            latestFiles[j + 1] = latestFiles[j];
                            latestFiles[j] = NULL;
                        } else {
                            /* File needs to be deleted as it can't be moved up. */
#ifdef _DEBUG
                            printf("    Delete old %s\n", latestFiles[j]);
                            fflush(NULL);
#endif
                            if (remove(latestFiles[j])) {
                                printf("Unable to delete old log file: %s (%s)\n",
                                    latestFiles[j], getLastErrorText());
                                fflush(NULL);
                            }
                            free(latestFiles[j]);
                            latestFiles[j] = NULL;
                        }
                    }
                }

#ifdef _DEBUG
                printf("    Insert at index  %d\n", i);
                fflush(NULL);
#endif
                latestFiles[i] = malloc(sizeof(char) * (strlen(testFile) + 1));
                strcpy(latestFiles[i], testFile);
                return;
            }
        }
    }
    
    /* File could not be added to the list because it was too old.  Delete. */
#ifdef _DEBUG
    printf("    Delete %s\n", testFile);
    fflush(NULL);
#endif
    if (remove(testFile)) {
        printf("Unable to delete old log file: %s (%s)\n",
            testFile, getLastErrorText());
        fflush(NULL);
    }
}

/**
 * Does a search for all files matching the specified pattern and deletes all
 *  but the most recent 'count' files.  The files are sorted by their names.
 */
void limitLogFileCount(const char *current, const char *pattern, int count) {
    char** latestFiles;
    int i;
#ifdef WIN32
    char* path;
    char* c;
    long handle;
    struct _finddata_t fblock;
    char* testFile;
#else
    glob_t g;
#endif

#ifdef _DEBUG
    printf("limitLogFileCount(%s, %d)\n", pattern, count);
    fflush(NULL);
#endif

    latestFiles = malloc(sizeof(char *) * count);
    for (i = 0; i < count; i++) {
        latestFiles[i] = NULL;
    }
    /* Set the first element to the current file so things will work correctly if it already
     *  exists. */
    latestFiles[0] = malloc(sizeof(char) * (strlen(current) + 1));
    strcpy(latestFiles[0], current);

#ifdef WIN32
    path = malloc(sizeof(char) * (strlen(pattern) + 1));

    /* Extract any path information from the beginning of the file */
    strcpy(path, pattern);
    c = max(strrchr(path, '\\'), strrchr(path, '/'));
    if (c == NULL) {
        path[0] = '\0';
    } else {
        c[1] = '\0'; /* terminate after the slash */
    }

    if ((handle = _findfirst(pattern, &fblock)) <= 0) {
        if (errno == ENOENT) {
            /* No matching files found. */
        } else {
            /* Encountered an error of some kind. */
            log_printf(WRAPPER_SOURCE_WRAPPER, LEVEL_ERROR,
                "Error in findfirst for log file purge: %s", pattern);
        }
    } else {
        testFile = malloc(sizeof(char) * (strlen(path) + strlen(fblock.name) + 1));
        sprintf(testFile, "%s%s", path, fblock.name);
        limitLogFileCountHandleFile(current, testFile, latestFiles, count);
        free(testFile);
        testFile = NULL;

        /* Look for additional entries */
        while (_findnext(handle, &fblock) == 0) {
            testFile = malloc(sizeof(char) * (strlen(path) + strlen(fblock.name) + 1));
            sprintf(testFile, "%s%s", path, fblock.name);
            limitLogFileCountHandleFile(current, testFile, latestFiles, count);
            free(testFile);
            testFile = NULL;
        }

        /* Close the file search */
        _findclose(handle);
    }

    free(path);
#else
    /* Wildcard support for unix */
    glob(pattern, GLOB_MARK | GLOB_NOSORT, NULL, &g);

    if (g.gl_pathc > 0) {
        for (i = 0; i < g.gl_pathc; i++) {
            limitLogFileCountHandleFile(current, g.gl_pathv[i], latestFiles, count);
        }
    } else {
        /* No matching files. */
    }

    globfree(&g);
#endif

#ifdef _DEBUG
    printf("  Sorted file list:\n");
    fflush(NULL);
#endif
    for (i = 0; i < count; i++) {
        if (latestFiles[i]) {
#ifdef _DEBUG
            printf("    latestFiles[%d]=%s\n", i, latestFiles[i]); fflush(NULL);
#endif
            free(latestFiles[i]);
        } else {
#ifdef _DEBUG
            printf("    latestFiles[%d]=NULL\n", i); fflush(NULL);
#endif
        }
    }
    free(latestFiles);
}

/**
 * Check to see whether or not the log file needs to be rolled.
 *  This is only called when synchronized.
 */
void checkAndRollLogs(const char *nowDate) {
    long position;
    struct stat fileStat;
    
    /* Depending on the roll mode, decide how to roll the log file. */
    if (logFileRollMode & ROLL_MODE_SIZE) {
        /* Roll based on the size of the file. */
        if (logFileMaxSize <= 0)
            return;
        
        /* Find out the current size of the file.  If the file is currently open then we need to
         *  use ftell to make sure that the buffered data is also included. */
        if (logfileFP != NULL) {
            /* File is open */
            if ((position = ftell(logfileFP)) < 0) {
                printf("Unable to get the current logfile size with ftell: %s\n", getLastErrorText());
                fflush(NULL);
                return;
            }
        } else {
            /* File is not open */
            if (stat(logFilePath, &fileStat) != 0) {
                if (getLastError() == 2) {
                    /* File does not yet exist. */
                    position = 0;
                } else if (getLastError() == 3) {
                    /* Path does not exist. */
                    position = 0;
                } else {
                    printf("Unable to get the current logfile size with stat: %s\n", getLastErrorText());
                    fflush(NULL);
                    return;
                }
            } else {
                position = fileStat.st_size;
            }
        }
        
        /* Does the log file need to rotated? */
        if (position >= logFileMaxSize) {
            rollLogs();
        }
    } else if (logFileRollMode & ROLL_MODE_DATE) {
        /* Roll based on the date of the log entry. */
        if (strcmp(nowDate, logFileLastNowDate) != 0) {
            /* The date has changed.  Close the file. */
            if (logfileFP != NULL) {
#ifdef _DEBUG
                printf("Closing logfile because the date changed...\n");
                fflush(NULL);
#endif
        
                fclose(logfileFP);
                logfileFP = NULL;
                currentLogFileName[0] = '\0';
            }
            
            /* This will happen just before a new log file is created.
             *  Check the maximum file count. */
            if (logFileMaxLogFiles > 0) {
                generateLogFileName(currentLogFileName, logFilePath, nowDate, NULL);
                generateLogFileName(workLogFileName, logFilePath, "????????", NULL);
                
                limitLogFileCount(currentLogFileName, workLogFileName, logFileMaxLogFiles + 1);
                
                currentLogFileName[0] = '\0';
                workLogFileName[0] = '\0';
            }
        }
    }
}

/*
 * Because of synchronization issues, it is not safe to immediately log messages
 *  that are logged from within signal handlers.  This is because it is possible
 *  that the signal was thrown while we were logging another message.
 *
 * To work around this, it is nessary to store such messages into a queue and
 *  then log them later when it is safe.
 *
 * Messages logged from signal handlers are relatively rare so this does not
 *  need to be all that efficient.
 */
void log_printf_queueInner( int source_id, int level, char *buffer ) {
#ifdef _DEBUG
    printf( "LOG ENQUEUE[%d]: %s\n", queueWriteIndex, buffer );
    fflush( NULL );
#endif

    /* Clear any old buffer, only necessary starting on the second time through the queue buffers. */
    if ( queueWrapped ) {
        free( queueMessages[queueWriteIndex] );
        queueMessages[queueWriteIndex] = NULL;
    }

    /* Store a reference to the buffer in the queue.  It will be freed later. */
    queueMessages[queueWriteIndex] = buffer;

    /* Store additional information about the call. */
    queueSourceIds[queueWriteIndex] = source_id;
    queueLevels[queueWriteIndex] = level;

    /* Lastly increment and wrap the write index. */
    queueWriteIndex++;
    if ( queueWriteIndex >= QUEUE_SIZE ) {
        queueWriteIndex = 0;
        queueWrapped = 1;
    }
}

void log_printf_queue( int useQueue, int source_id, int level, const char *lpszFmt, ... ) {
    va_list     vargs;
    int         count;
    char        *buffer;
    int         bufferSize = 100;
    int         itFit = 0;

    /* Start by processing any arguments so that we can store a simple string. */

    /* This is a pain to do efficiently without using a static buffer.  But
     *  this call is only used in cases where we can not safely use such buffers.
     *  We do not know how big a buffer we need, so loop, growing it until we get
     *  a size that works.  The initial size will be big enough for most messages
     *  that this function is called with, but not so big as to be any less
     *  efficient than necessary. */
    do {
        buffer = malloc( sizeof( char ) * bufferSize );

        /* Before we can store the string, we need to know how much space is required to do so. */
        va_start( vargs, lpszFmt );
#ifdef WIN32
        count = _vsnprintf( buffer, bufferSize, lpszFmt, vargs );
#else
        count = vsnprintf( buffer, bufferSize, lpszFmt, vargs );
#endif
        va_end( vargs );

        /*
        printf( "count: %d bufferSize=%d\n", count, bufferSize );
        fflush(NULL);
        */

        /* On UNIX, the required size will be returned if it is too small.
         *  On Windows however, we always get -1.  Even worse, if the size
         *  is exactly correct then the buffer will not be null terminated.
         *  In either case, resize the buffer as best we can and retry. */
        if ( count < 0 ) {
            /* Not big enough, expand the buffer size and try again. */
            bufferSize += 100;
        } else if ( count >= bufferSize ) {
            /* Not big enough, but we know how big it will need to be. */
            bufferSize = count + 1;
        } else {
            itFit = 1;
        }

        if ( !itFit ) {
            /* Will need to try again, so free the buffer. */
            free( buffer );
            buffer = NULL;
        }
    } while ( !itFit );

    /* Now decide what to actually do with the message. */
    if ( useQueue ) {
        log_printf_queueInner( source_id, level, buffer );
        /* The buffer will be freed by the queue at a later point. */
    } else {
        /* Use the normal logging function.  There is some extra overhead
         *  here because the message is expanded twice, but this greatly
         *  simplifies the code over other options and makes it much less
         *  error prone. */
        log_printf( source_id, level, "%s", buffer );
        free( buffer );
    }
}

/**
 * Perform any required logger maintenance at regular intervals.
 *
 * One operation is to log any queued messages.  This must be done very
 *  carefully as it is possible that a signal handler could be thrown at
 *  any time as this function is being executed.
 */
void maintainLogger() {
    /* Empty the queue of any logged messages. */
    while ( queueReadIndex != queueWriteIndex ) {
#ifdef _DEBUG
        printf( "LOG QUEUED[%d]: %s\n", queueReadIndex, queueMessages[queueReadIndex] );
        fflush( NULL );
#endif
        log_printf( queueSourceIds[queueReadIndex], queueLevels[queueReadIndex],
            "%s", queueMessages[queueReadIndex] );
        queueReadIndex++;
        if ( queueReadIndex >= QUEUE_SIZE ) {
            queueReadIndex = 0;
        }
    }
}
