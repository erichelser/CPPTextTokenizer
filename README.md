# CPPTextTokenizer

##Purpose:

This program is used to convert a .H or .CPP file (or any similarly formatted C++-style code file) into a series of individual tokens. These tokens can then be compared with a diff tool such as Beyond Compare, in order to see the true changes made in a code file. Since this tool ignores whitespace, comments, and formatting style, you will not see false-positives from the diff tool in the resulting text.

##Usage:

- To recompile the program, run "CPPTextTokenizer_Compiler.bat" from the command line. This shouldn't be necessary unless your JRE/JDK version is too old/new.

- To execute the program, open up a command prompt window, and type "CPPTextTokenizer.bat" followed by the name of the file you wish to convert. If no argument is provided, the program will crash with an Array Index Out Of Bounds exception. If multiple arguments are provided, only the first one will be used.

##Output:

If successful, the program will dump the list of tokens it found, one per line, in the following format:

1. Type of token found (keyword, literal string, symbol, etc.)

2. Tab character

3. The actual text from the input file.


Eric Helser
April 2014
