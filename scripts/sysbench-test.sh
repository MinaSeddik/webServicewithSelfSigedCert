#!/bin/bash


https://severalnines.com/blog/how-benchmark-performance-mysql-mariadb-using-sysbench/
https://mariadb.com/kb/en/sysbench-benchmark-setup/



#The sysbench CPU benchmark
#********************************
#The result simply indicates the total time required to calculate the primes
sysbench --test=cpu --cpu-max-prime=20000 run
#==================================================================
#The sysbench file I/O benchmark
#********************************
#This creates files in the current working directory, which the run step will read and write.
sysbench --test=fileio --file-total-size=150G prepare
sysbench --test=fileio --file-total-size=150G --file-test-mode=rndrw --init-rng=on --max-time=300 --max-requests=0 run
#run a cleanup to delete the files sysbench created for the benchmarks:
sysbench --test=fileio --file-total-size=150G cleanup
#==================================================================
#The sysbench OLTP benchmark
#********************************
#The first step is to prepare a table for the test:
sysbench --db-driver=mysql --mysql-user=mina --mysql-password=#Mina_1234$ \
 --mysql-db=test --range_size=100 \
  --table_size=10000 --tables=2 --threads=1 --events=0 --time=60 \
  --rand-type=uniform /usr/share/sysbench/oltp_read_only.lua prepare
#Then you can run the benchmark as follows
sysbench --db-driver=mysql --mysql-user=mina --mysql-password=#Mina_1234$ \
  --mysql-db=test --range_size=100 \
  --table_size=10000 --tables=2 --threads=1 --events=0 --time=60 \
  --rand-type=uniform /usr/share/sysbench/oltp_read_only.lua run
  #==================================================================