DIGITAL CERTIFICATE GENERATOR  -  Java Mini Project
====================================================

Requirements : JDK 11 or later (developed and tested with JDK 21)

Compile      : javac *.java
Run          : java Main

Generated certificates are saved in the folder "certificates":
  CERT-0001.png / CERT-0001.txt ...   one file per participant and format
  generation_report.txt               summary of a batch run

CSV import format (option 3) - one participant per line:
  name,email,department
See participants.csv for an example (it deliberately contains 3 bad rows
to show the error report).
