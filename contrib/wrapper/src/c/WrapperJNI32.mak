# Microsoft Developer Studio Generated NMAKE File, Based on WrapperJNI32.dsp
!IF "$(CFG)" == ""
CFG=WrapperJNI - Win32 Debug
!MESSAGE Build mode not specified.  Defaulting to "WrapperJNI - Win32 Debug".
!ENDIF 

!IF "$(CFG)" != "WrapperJNI - Win32 Release" && "$(CFG)" != "WrapperJNI - Win32 Debug"
!MESSAGE The build target "$(CFG)" is invalid.
!MESSAGE Usage:
!MESSAGE 
!MESSAGE NMAKE /f "WrapperJNI32.mak" CFG="WrapperJNI - Win32 Debug"
!MESSAGE 
!MESSAGE Valid build modes:
!MESSAGE 
!MESSAGE "WrapperJNI - Win32 Release" ("Win32 (x86) Dynamic-Link Library")
!MESSAGE "WrapperJNI - Win32 Debug" ("Win32 (x86) Dynamic-Link Library")
!MESSAGE 
!ERROR Ivalid build mode.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "WrapperJNI - Win32 Release"

OUTDIR=.\WrapperJNI___Win32_Release32
INTDIR=.\WrapperJNI___Win32_Release32

ALL : "..\..\lib\wrapper.dll"


CLEAN :
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\wrapperinfo.obj"
	-@erase "$(INTDIR)\wrapperjni.obj"
	-@erase "$(INTDIR)\wrapperjni_unix.obj"
	-@erase "$(INTDIR)\wrapperjni_win.obj"
	-@erase "$(OUTDIR)\wrapper.exp"
	-@erase "$(OUTDIR)\wrapper.lib"
	-@erase "..\..\lib\wrapper.dll"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MT /W3 /GX /O2 /I "C:\Sun\j2sdk1.4.2_08\jre\..\include\\" /I "C:\Sun\j2sdk1.4.2_08\jre\..\include\win32\\" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "WRAPPERJNI_EXPORTS" /Fp"$(INTDIR)\WrapperJNI32.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\WrapperJNI32.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /incremental:no /pdb:"$(OUTDIR)\wrapper.pdb" /machine:I386 /out:"../../lib/wrapper.dll" /implib:"$(OUTDIR)\wrapper.lib" 
LINK32_OBJS= \
	"$(INTDIR)\wrapperinfo.obj" \
	"$(INTDIR)\wrapperjni.obj" \
	"$(INTDIR)\wrapperjni_unix.obj" \
	"$(INTDIR)\wrapperjni_win.obj"

"..\..\lib\wrapper.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "WrapperJNI - Win32 Debug"

OUTDIR=.\WrapperJNI___Win32_Debug32
INTDIR=.\WrapperJNI___Win32_Debug32
# Begin Custom Macros
OutDir=.\WrapperJNI___Win32_Debug32
# End Custom Macros

ALL : "..\..\lib\wrapper.dll" "$(OUTDIR)\WrapperJNI32.bsc"


CLEAN :
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(INTDIR)\wrapperinfo.obj"
	-@erase "$(INTDIR)\wrapperinfo.sbr"
	-@erase "$(INTDIR)\wrapperjni.obj"
	-@erase "$(INTDIR)\wrapperjni.sbr"
	-@erase "$(INTDIR)\wrapperjni_unix.obj"
	-@erase "$(INTDIR)\wrapperjni_unix.sbr"
	-@erase "$(INTDIR)\wrapperjni_win.obj"
	-@erase "$(INTDIR)\wrapperjni_win.sbr"
	-@erase "$(OUTDIR)\wrapper.exp"
	-@erase "$(OUTDIR)\wrapper.lib"
	-@erase "$(OUTDIR)\wrapper.pdb"
	-@erase "$(OUTDIR)\WrapperJNI32.bsc"
	-@erase "..\..\lib\wrapper.dll"
	-@erase "..\..\lib\wrapper.ilk"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MTd /W3 /Gm /GX /ZI /Od /I "C:\Sun\j2sdk1.4.2_08\jre\..\include\\" /I "C:\Sun\j2sdk1.4.2_08\jre\..\include\win32\\" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "WRAPPERJNI_EXPORTS" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\WrapperJNI32.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\WrapperJNI32.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\wrapperinfo.sbr" \
	"$(INTDIR)\wrapperjni.sbr" \
	"$(INTDIR)\wrapperjni_unix.sbr" \
	"$(INTDIR)\wrapperjni_win.sbr"

"$(OUTDIR)\WrapperJNI32.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /incremental:yes /pdb:"$(OUTDIR)\wrapper.pdb" /debug /machine:I386 /out:"../../lib/wrapper.dll" /implib:"$(OUTDIR)\wrapper.lib" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\wrapperinfo.obj" \
	"$(INTDIR)\wrapperjni.obj" \
	"$(INTDIR)\wrapperjni_unix.obj" \
	"$(INTDIR)\wrapperjni_win.obj"

"..\..\lib\wrapper.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("WrapperJNI32.dep")
!INCLUDE "WrapperJNI32.dep"
!ELSE 
!MESSAGE Warning: cannot find "WrapperJNI32.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "WrapperJNI - Win32 Release" || "$(CFG)" == "WrapperJNI - Win32 Debug"
SOURCE=.\wrapperinfo.c

!IF  "$(CFG)" == "WrapperJNI - Win32 Release"


"$(INTDIR)\wrapperinfo.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "WrapperJNI - Win32 Debug"


"$(INTDIR)\wrapperinfo.obj"	"$(INTDIR)\wrapperinfo.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\wrapperjni.c

!IF  "$(CFG)" == "WrapperJNI - Win32 Release"


"$(INTDIR)\wrapperjni.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "WrapperJNI - Win32 Debug"


"$(INTDIR)\wrapperjni.obj"	"$(INTDIR)\wrapperjni.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\wrapperjni_unix.c

!IF  "$(CFG)" == "WrapperJNI - Win32 Release"


"$(INTDIR)\wrapperjni_unix.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "WrapperJNI - Win32 Debug"


"$(INTDIR)\wrapperjni_unix.obj"	"$(INTDIR)\wrapperjni_unix.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\wrapperjni_win.c

!IF  "$(CFG)" == "WrapperJNI - Win32 Release"


"$(INTDIR)\wrapperjni_win.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "WrapperJNI - Win32 Debug"


"$(INTDIR)\wrapperjni_win.obj"	"$(INTDIR)\wrapperjni_win.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 


!ENDIF 

