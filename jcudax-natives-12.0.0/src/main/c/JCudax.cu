#include <cuda_runtime.h>
#include <cmath>
#include <stdio.h>
#include <jni.h>
#include "fun_asgc_easyai_extensions_cuda_JCudax.h"


extern "C" {

__global__ void matrixSoftMaxPdKernel(double* qkt, double* errorMatrix, double* grMatrix, int x, int y, double param) {
    int i = blockIdx.x * blockDim.x + threadIdx.x; // 行索引
    int j = blockIdx.y * blockDim.y + threadIdx.y; // 列索引

    if (i < x && j < y) {
        int z = y;  // qr 的列数等于 y
        double jValue = qkt[i * y + j];
        double sigma = 0;

        for (int k = 0; k < z; ++k) {
            double kValue = qkt[i * y + k];
            double error = errorMatrix[i * y + k];
            double er;

            if (k != j) {
                er = -error * kValue * jValue;
            } else {
                er = jValue * (1.0 - jValue) * error;
            }

            sigma += er;
        }

        grMatrix[i * y + j] = sigma / param;
    }
}

void matrixSoftMaxPd(double* h_qkt, double* h_errorMatrix, double* h_grMatrix,  int x, int y, double wordVectorDimension) {
    double param = sqrt(wordVectorDimension);
    size_t size = x * y * sizeof(double);

    double *d_qkt, *d_errorMatrix, *d_grMatrix;

    // 分配 GPU 内存
    cudaMalloc(&d_qkt, size);
    cudaMalloc(&d_errorMatrix, size);
    cudaMalloc(&d_grMatrix, size);

    // 复制数据到 GPU
    cudaMemcpy(d_qkt, h_qkt, size, cudaMemcpyHostToDevice);
    cudaMemcpy(d_errorMatrix, h_errorMatrix, size, cudaMemcpyHostToDevice);

    // 定义线程和块
    dim3 blockSize(16, 16);
    dim3 gridSize((x + blockSize.x - 1) / blockSize.x, (y + blockSize.y - 1) / blockSize.y);

    // 启动 CUDA 内核
    matrixSoftMaxPdKernel<<<gridSize, blockSize>>>(d_qkt, d_errorMatrix, d_grMatrix, x, y, param);

    // 复制结果回主机
    cudaMemcpy(h_grMatrix, d_grMatrix, size, cudaMemcpyDeviceToHost);

    // 释放 GPU 内存
    cudaFree(d_qkt);
    cudaFree(d_errorMatrix);
    cudaFree(d_grMatrix);
}



JNIEXPORT void JNICALL Java_fun_asgc_easyai_extensions_cuda_JCudax_matrixSoftMaxPd  (
    JNIEnv* env, jclass cls,
    jdoubleArray j_qkt, jdoubleArray j_errorMatrix, jdoubleArray j_grMatrix,
    jint x, jint y, jdouble wordVectorDimension) {

    // 获取 Java 数组数据
    jdouble* h_qkt = env->GetDoubleArrayElements(j_qkt, NULL);
    jdouble* h_errorMatrix = env->GetDoubleArrayElements(j_errorMatrix, NULL);
    jdouble* h_grMatrix = env->GetDoubleArrayElements(j_grMatrix, NULL);

    // 调用 CUDA 实现
    matrixSoftMaxPd(h_qkt, h_errorMatrix, h_grMatrix, x, y, wordVectorDimension);

    // 将结果同步回 Java 数组
    env->ReleaseDoubleArrayElements(j_qkt, h_qkt, 0);
    env->ReleaseDoubleArrayElements(j_errorMatrix, h_errorMatrix, 0);
    env->ReleaseDoubleArrayElements(j_grMatrix, h_grMatrix, 0);
}

}