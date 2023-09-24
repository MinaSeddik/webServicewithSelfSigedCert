
set -u;     # raise an error when there’s an undefined variable
set -e;     # Exit immediately if a command exits with a non-zero status.


# display the data while it gets written to the file
tail -f yourfile.txt

# display the updated size of the file as it grows
watch ls -lh yourfile.txt
# by default it gets updated every 2 seconds
# to update it every 5 seconds; use -n option
sudo watch -n 5 ls -lh yourfile.txt


# redirect error
command 2>/dev/null

# redirect error and output
command 2>&1 /dev/null

# find all files containing specific text (string) on Linux?
grep -rnw '/path/to/somewhere/' -e 'pattern'
#-r or -R is recursive,
#-n is line number, and
#-w stands for match the whole word.
#-l (lower-case L) can be added to just give the file name of matching files.
#-e is the pattern used during the search
