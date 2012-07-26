README for data file.

First, the data file for the current Dust & Magnet should be tab-delimited text file. Since the Dust & Magnet data import routine is so primitive, you should provide more detailed information about the data file. That's why you will find four lines of the header in the sample data file (cereal.txt). 

First line: magnet names (or attributes names)
Second line: variable type (S: String, I: Integer, and D: Double)
Third line: attribute type (N: Nominal, O (not zero): Ordinal, Q: Quantitative)
Fourth line: 0 (when the third line is N or Q) or a list of categories delimited by "/" (when the third line is O)

I meant to implement a smart function to automatically determine the values for second, third, and fourth lines, but it was not implemented on time.
