#!/bin/bash

#if needed
#mysqldump --max_allowed_packet=1G
-------------------------------------------------------------
#mysqldump will backup by default all the triggers but NOT the stored procedures/functions. There are 2 mysqldump parameters that control this behavior:
#
#--routines - FALSE by default
#--triggers - TRUE by default
-------------------------------------------------------------
#-- some usfull options
#--opt  // page 690
#--allow-keywords, --quote-names
#--complete-insert
#--tz-utc
#--lock-all-tables
#--tab
#--skip-extended-insert
#--single-transaction
#--master-data
-------------------------------------------------------------
# dump myapp database
mysqldump --host=127.0.0.1 --protocol=TCP --port=3306 --user=mina --password=#Mina_1234$ \
  --result-file=./myapp-full-backup-$(date +%F).sql --databases myapp \
  --events \
  --triggers \
  --routines \
  --create-options \
  --dump-date \
  --verbose

# dump all databases
mysqldump --host=127.0.0.1 --protocol=TCP --port=3306 --user=mina --password=#Mina_1234$ \
  --result-file=./all.sql \
  --all-databases \
  --events \
  --triggers \
  --routines \
  --create-options \
  --dump-date \
  --verbose

# dump specific table
mysqldump --host=127.0.0.1 --protocol=TCP --port=3306 --user=mina --password=#Mina_1234$ \
  --result-file=./myapp_orders.sql \
  --events \
  --triggers \
  --routines \
  --create-options \
  --verbose \
  myapp Orders

# --lock-table good for myisam
# --single-transaction good for innodb

# --single-transaction --quick --lock-table --flush-logs
# what is the default value

#Use of --opt is the same as specifying --add-drop-table, --add-locks, --create-options,
#--disable-keys, --extended-insert, --lock-tables, --quick, and --set-charset.
#All of the options that --opt stands for also are on by default because --opt is on by default.
