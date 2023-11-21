#!/bin/bash


#https://www.oracle.com/technical-resources/articles/java/architect-benchmarking.html

# We use only one fork (-f 1).
# We run five warm-up iterations (-wi 5).
# We run five iterations of 3 seconds each (-i 5 -r 3s).
# We tune the JVM configuration with jvmArgs.
# We run all benchmarks whose class name matches the .*Benchmark.* regular expression.



#java -jar ../target/benchmarks.jar benchmarck_jmh.BenchmarkMain.testIntern$
java -jar ../target/benchmarks.jar -f 1 -w 1 -i 1 benchmarck_jmh.BenchmarkMain.testInternWithoutParameter$ |& tee benchmark.out


#java -jar ../target/benchmarks.jar benchmarck_jmh.BenchmarkMain.testIntern$ --jvmArgs -Djava.util.concurrent.ForkJoinPool.common.parallelism=${i} -p corePoolSize=${i} |& tee out.${i}

# comma separated benchmark names to run
#java -jar ../target/benchmarks.jar benchmarck_jmh.BenchmarkMain.testIntern$, benchmarck_jmh.AnotherBenchMarkwithParameter.heap$ --jvmArgs -Djava.util.concurrent.ForkJoinPool.common.parallelism=${i} -p corePoolSize=${i} |& tee out.${i}


