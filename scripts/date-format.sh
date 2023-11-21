#!/bin/bash

echo "'date' command is used to get the system date"
echo -ne "Example:\ndate\n\n"

echo "Date Format: default"
date

echo -ne "\nDate command: \ndate [option]... [+format]\n\n"

cat << EOF
%D – Display date as mm/dd/yy"
%Y – Year (e.g., 2020)
%m – Month (01-12)
%B – Long month name (e.g., November)
%b – Short month name (e.g., Nov)
%d – Day of month (e.g., 01)
%j – Day of year (001-366)
%u – Day of week (1-7)
%A – Full weekday name (e.g., Friday)
%a – Short weekday name (e.g., Fri)
%H – Hour (00-23)
%I – Hour (01-12)
%M – Minute (00-59)
%S – Second (00-60)
EOF


echo -ne "\n\n\nExamples: \n\n"

date +"Year: %Y, Month: %m, Day: %d"
date "+DATE: %D%nTIME: %T"
date "+DATE: %D%tTIME: %T"
date +"Week number: %V Year: %y"
date +"%Y-%m-%d"
date +"%Y-%m-%d %H:%M:%S"
date +"%Y-%m-%d"

echo -ne "\n\ndate assigned to variable:\n"

thisDate=$(date +"%Y-%m-%d %H:%M:%S")
fileName=myfile_$(date +"%Y-%m-%d %H:%M:%S")_$(date +"Week number: %V Year: %y").txt

echo $thisDate
echo $fileName