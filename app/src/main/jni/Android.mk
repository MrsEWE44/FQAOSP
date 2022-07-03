LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libterm

LOCAL_SRC_FILES := term.cpp

LOCAL_CPP_INCLUDES += $(LOCAL_PATH)


include $(BUILD_SHARED_LIBRARY)




