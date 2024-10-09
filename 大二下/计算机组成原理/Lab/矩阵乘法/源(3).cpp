#include<iostream>
#include<time.h>
#include<omp.h>

using namespace std;
#define REAL_T double 

void printFlops(int A_height, int B_width, int B_height, clock_t start, clock_t stop) {
	REAL_T flops = (2.0 * A_height * B_width * B_height) / 1E9 / ((stop - start) / (CLOCKS_PER_SEC * 1.0));
	cout << "GFLOPS:\t" << flops << endl;
}

void initMatrix(int n, REAL_T* A, REAL_T* B, REAL_T* C) {
	for (int i = 0; i < n; ++i)
		for (int j = 0; j < n; ++j) {
			A[i + j * n] = (i + j + (i * j) % 100) % 100;
			B[i + j * n] = ((i - j) * (i - j) + (i * j) % 200) % 100;
			C[i + j * n] = 0;
		}
}

void dgemm(int n, REAL_T* A, REAL_T* B, REAL_T* C) {
	for (int i = 0; i < n; ++i)
		for (int j = 0; j < n; ++j) {
			REAL_T cij = C[i + j * n];
			for (int k = 0; k < n; k++) {
				cij += A[i + k * n] * B[k + j * n];
			}
			C[i + j * n] = cij;
		}
}

#define UNROLL (4)
void pavx_dgemm(int n, REAL_T* A, REAL_T* B, REAL_T* C) {
	for (int i = 0; i < n; i += UNROLL)
		for (int j = 0; j < n; ++j) {
			REAL_T c1 = C[i + j * n];
			REAL_T c2 = C[i + 1 + j * n];
			REAL_T c3 = C[i + 2 + j * n];
			REAL_T c4 = C[i + 3 + j * n];
			for (int k = 0; k < n; k++) {
				c1 += A[i + k * n] * B[k + j * n];
				c2 += A[i + 1 + k * n] * B[k + j * n];
				c3 += A[i + 2 + k * n] * B[k + j * n];
				c4 += A[i + 3 + k * n] * B[k + j * n];
			}
			C[i + j * n] = c1;
			C[i + 1 + j * n] = c2;
			C[i + 2 + j * n] = c3;
			C[i + 3 + j * n] = c4;
		}
}

#define BLOCKSIZE (32)
void do_block(int n, int si, int sj, int sk, REAL_T* A, REAL_T* B, REAL_T* C) {
	for (int i = si; i < si + BLOCKSIZE; i += 4)
		for (int j = sj; j < sj + BLOCKSIZE; ++j) {
			REAL_T c1 = C[i + j * n];
			REAL_T c2 = C[i + 1 + j * n];
			REAL_T c3 = C[i + 2 + j * n];
			REAL_T c4 = C[i + 3 + j * n];

			for (int k = sk; k < sk + BLOCKSIZE; ++k) {
				c1 += A[i + k * n] * B[k + j * n];
				c2 += A[i + 1 + k * n] * B[k + j * n];
				c3 += A[i + 2 + k * n] * B[k + j * n];
				c4 += A[i + 3 + k * n] * B[k + j * n];
			}

			C[i + j * n] = c1;
			C[i + 1 + j * n] = c2;
			C[i + 2 + j * n] = c3;
			C[i + 3 + j * n] = c4;
		}
}


void block_gemm(int n, REAL_T* A, REAL_T* B, REAL_T* C) {
	for (int sj = 0; sj < n; sj += BLOCKSIZE)
		for (int si = 0; si < n; si += BLOCKSIZE)
			for (int sk = 0; sk < n; sk += BLOCKSIZE)
				do_block(n, si, sj, sk, A, B, C);
}

void omp_gemm(int n, REAL_T* A, REAL_T* B, REAL_T* C) {
#pragma omp parallel for
	for (int sj = 0; sj < n; sj += BLOCKSIZE)
		for (int si = 0; si < n; si += BLOCKSIZE)
			for (int sk = 0; sk < n; sk += BLOCKSIZE)
				do_block(n, si, sj, sk, A, B, C);
}

int main()
{
	REAL_T* A, * B, * C;
	clock_t start, stop;
	int n = 1024;
	A = new REAL_T[n * n];
	B = new REAL_T[n * n];
	C = new REAL_T[n * n];
	initMatrix(n, A, B, C);

	cout << "origin caculation begin...\n";
	start = clock();
	dgemm(n, A, B, C);
	stop = clock();
	cout << (stop - start) / CLOCKS_PER_SEC << "." << (stop - start) % CLOCKS_PER_SEC << "\t\t";
	printFlops(n, n, n, start, stop);

	initMatrix(n, A, B, C);
	cout << "parallel caculation begin...\n";
	start = clock();
	pavx_dgemm(n, A, B, C);
	stop = clock();
	cout << (stop - start) / CLOCKS_PER_SEC << "." << (stop - start) % CLOCKS_PER_SEC << "\t\t";
	printFlops(n, n, n, start, stop);

	initMatrix(n, A, B, C);
	cout << "blocked parallel caculation begin...\n";
	start = clock();
	block_gemm(n, A, B, C);
	stop = clock();
	cout << (stop - start) / CLOCKS_PER_SEC << "." << (stop - start) % CLOCKS_PER_SEC << "\t\t";
	printFlops(n, n, n, start, stop);

	initMatrix(n, A, B, C);
	cout << "OpenMP blocked parallel caculation begin...\n";
	double begin = omp_get_wtime();
	omp_gemm(n, A, B, C);
	double end = omp_get_wtime();
	cout << float(end - begin) << "\t\t";
	REAL_T flops = (2.0 * n * n * n) / 1E9 / (end - begin);
	cout << "GFLOPS:\t" << flops << endl;
	return 1;

}