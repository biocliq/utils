package com.biocliq.fluwiz.dataloader.xls;

public class TestMain2 {

	public static void main(String[] args) {
		int[] A = {10,-10,-1,-1,10 };
		int[] B = {-1,-1,-1,1,1,1,1};
		int[] C = {5,-2,-3,1};
		int[] D = {1,-2,1};
		int[] E = {0};
		int[] F = {5};
		int[] G = { 234, 25, -234, -234, 25, 208, 200};

		System.out.println(solution(A));
		System.out.println(solution(B));
		System.out.println(solution(C));
		System.out.println(solution(D));
		System.out.println(solution(E));
		System.out.println(solution(F));
		System.out.println(solution(G));
	}

	public static int solution(int[] A) {
		int moves = 0;
		int accumulation = 0;
		int temp = 0;
		int size = A.length;
		if (size < 2)
			return 0;
		for (int i = 0; i < size; i++) {
			temp = accumulation + A[i];
			if (temp < 0) {
				int result = findNextPositive(A, i, temp);
				if (result > 0)
					accumulation += result;
				else {
					accumulation += swapNextPositive(A, i);				
				}
				moves++;
			} else {
				accumulation += A[i];
			}
		}
		return moves;
	}
	
	private static int swapNextPositive(int[] a, int i) {
		int temp = a[i];
		int k = 0;
		for (k = i; k < a.length; k++) {
			if (a[k] > 0) {
				a[i] = a[k];
				a[k] = temp;
				return a[i];
			}
		}
		return -1;
	}
	
	private static int findNextPositive(int[] a, int i, int cur) {
		int temp = a[i];
		int k = 0;
		for (k = i; k < a.length; k++) {
			if (cur + a[k] > 0) {
				a[i] = a[k];
				a[k] = temp;
				return a[i];
			}
		}
		return -1;
	}
}
