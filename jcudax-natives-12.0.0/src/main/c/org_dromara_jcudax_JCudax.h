/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_dromara_jcudax_JCudax */

#ifndef _Included_org_dromara_jcudax_JCudax
#define _Included_org_dromara_jcudax_JCudax
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_dromara_jcudax_JCudax
 * Method:    matrixSoftMaxPd
 * Signature: ([D[D[DIID)V
 */
JNIEXPORT void JNICALL Java_org_dromara_jcudax_JCudax_matrixSoftMaxPd
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint, jdouble);

/*
 * Class:     org_dromara_jcudax_JCudax
 * Method:    matrixSoftMaxPdFp32
 * Signature: ([F[F[FIIF)V
 */
JNIEXPORT void JNICALL Java_org_dromara_jcudax_JCudax_matrixSoftMaxPdFp32
  (JNIEnv *, jclass, jfloatArray, jfloatArray, jfloatArray, jint, jint, jfloat);

#ifdef __cplusplus
}
#endif
#endif
