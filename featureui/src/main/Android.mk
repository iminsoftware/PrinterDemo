LOCAL_PATH:= $(call my-dir)

# Reusable Camera performance test classes and helpers
include $(CLEAR_VARS)

LOCAL_MODULE := bncommonui

LOCAL_MODULE_TAGS := optional
LOCAL_USE_AAPT2 := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.viewpager2_viewpager2 \
    androidx.annotation_annotation \
    androidx.recyclerview_recyclerview \
    androidx.appcompat_appcompat \
    androidx.coordinatorlayout_coordinatorlayout \
    androidx-constraintlayout_constraintlayout \
    com.google.android.material_material \



LOCAL_MANIFEST_FILE := AndroidManifest.xml

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_SRC_FILES := $(call all-java-files-under, java)

LOCAL_SDK_VERSION := current
LOCAL_MIN_SDK_VERSION := 19

# ==================================================
#include $(call all-makefiles-under,$(LOCAL_PATH))
include $(BUILD_STATIC_JAVA_LIBRARY)
