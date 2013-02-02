
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class ArithmaticsBenchmark extends SimpleBenchmark {
    final static int powOf2 = 2 << 8;
    final static int modPowOf2Mask = powOf2 - 1;

    public int timeBranchAdd(int reps) {
	int count = 0;
	int count1 = 0;
	for (int i = 0; i < reps; i++) {
	    if ((i & 1) == 0) {
		count += i;
	    } else {
		count1 += i;
	    }
	}
	return count + count1;
    }

    public int timeHalfBranchAdd(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    if ((i & 1) == 0) {
		count += i;
	    }
	}
	return count;
    }

    public int timeBranchAddSame(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    if ((i & 1) == 0) {
		count += i;
	    } else {
		count += i;
	    }
	}
	return count;
    }

    public int timeModParallel(int reps) {
	int count0 = 0;
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for (int i = 0; i < reps; i++) {
	    count0 += i % 12321;
	    count1 += i + 1;
	    count2 += i + 2;
	    count3 += i + 3;
	}
	return count0 + count1 + count2 + count3;
    }

    public int timeModPow2Parallel(int reps) {
	int count0 = 0;
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for (int i = 0; i < reps; i++) {
	    count0 += i % powOf2;
	    count1 += i + 1;
	    count2 += i + 2;
	    count3 += i + 3;
	}
	return count0 + count1 + count2 + count3;
    }

    public int timeModTrickParallel(int reps) {
	int count0 = 0;
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for (int i = 0; i < reps; i++) {
	    count0 += i & modPowOf2Mask;
	    count1 += i + 1;
	    count2 += i + 2;
	    count3 += i + 3;
	}
	return count0 + count1 + count2 + count3;
    }

    public int timeMod(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i % 12321;
	}
	return count;
    }

    public int timePow2Mod(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i % powOf2;
	}
	return count;
    }

    public int timeModTrick(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i & modPowOf2Mask;
	}
	return count;
    }

    public int timeParallelAdd(int reps) {
	int count0 = 0;
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for (int i = 0; i < reps; i++) {
	    count0 += i;
	    count1 += i + 1;
	    count2 += i + 2;
	    count3 += i + 3;
	}
	return count0 + count1 + count2 + count3;
    }

    public int timeAdd(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i;
	}
	return count;
    }

    public int timeSub(int reps) {
	int count = Integer.MAX_VALUE;
	for (int i = 0; i < reps; i++) {
	    count -= i;
	}
	return count;
    }

    public int timeAddNegative(int reps) {
	int count = Integer.MAX_VALUE;
	for (int i = -reps; i < -1; i++) {
	    count += i;
	}
	return count;
    }

    public int timeKMul(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += 213423 * i;
	}
	return count;
    }

    public int timeMulK(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i * 213423;
	}
	return count;
    }

    public int timeMulMul(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i * i;
	}
	return count;
    }

    public int timePow2(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += Math.pow(i, 2);
	}
	return count;
    }

    public int timeMul2(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += 2 * i;
	}
	return count;
    }

    public int timeMul2Shift(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i << 1;
	}
	return count;
    }

    public int timeDivI(int reps) {
	int count = 0;
	for (int i = 2; i < reps + 2; i++) {
	    count += Integer.MAX_VALUE / i;
	}
	return count;
    }

    public int timeIDiv(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i / 123423;
	}
	return count;
    }

    public int timeDiv2(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i / 2;
	}
	return count;
    }

    public int timeDiv2Shift(int reps) {
	int count = 0;
	for (int i = 0; i < reps; i++) {
	    count += i >> 1;
	}
	return count;
    }

    public static void main(String[] args) throws Exception {
	Runner.main(ArithmaticsBenchmark.class, args);
    }
}
