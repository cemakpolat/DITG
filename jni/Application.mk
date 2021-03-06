APP_OPTIM := release
#APP_PLATFORM := android-9
APP_PLATFORM := android-16
#APP_STL:=stlport_static
#APP_CPPFLAGS= -std=gnu++0x
APP_STL := gnustl_static
#APP_CPPFLAGS := -frtti -DCC_ENABLE_CHIPMUNK_INTEGRATION=1 -DCOCOS2D_DEBUG=1 -std=c++11 -DDEBUG=1  
# I disable the debugs by assigning debug to 0
APP_CPPFLAGS := -frtti -DCC_ENABLE_CHIPMUNK_INTEGRATION=1 -DCOCOS2D_DEBUG=0 -std=c++11 -DDEBUG=0

APP_USE_CPP0X := true
LOCAL_CPP_FEATURES += rtti # Enable exceptions in Android.mk
APP_CPPFLAGS += -fexceptions     # Enable exceptions in Application.mk
LOCAL_CPP_FEATURES += exceptions # Enable exceptions in Android.mk

