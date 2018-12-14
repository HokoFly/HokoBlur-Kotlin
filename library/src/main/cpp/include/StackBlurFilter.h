#ifndef HOKO_BLUR_STACKBLURFILTER_H
#define HOKO_BLUR_STACKBLURFILTER_H

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "BlurUtil.h"
#include <android/log.h>
#include <android/bitmap.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_hoko_ktblur_filter_NativeBlurFilter_nativeStackBlur
        (JNIEnv *, jobject, jobject, jint, jint, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
