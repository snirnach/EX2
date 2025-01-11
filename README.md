#Spreadsheet Application (Ex2)

Overview

This project is a simple spreadsheet application that supports text, numbers, and formulas. It includes functionality to evaluate expressions, detect cyclic dependencies, and handle various input formats.


Features

Basic Data Storage: Each cell can store numbers, text, or formulas.

Formula Evaluation: Supports arithmetic operations and cell references.

Error Handling: Detects and displays errors such as cyclic dependencies (ERR_CYCLE) and invalid formulas (ERR_FORM).

File Operations: Load and save spreadsheets from a file.

Automatic Recalculation: Updates cell values dynamically when dependencies change.


Installation

To run the project:

Ensure you have Java 8+ installed.

Compile the Java files:

javac assignments/ex2/*.java

Run the application:

java assignments.ex2.Ex2GUI


Usage

Click on a cell and enter values, text, or formulas.

Formulas should start with = (e.g., =A1+B2).

Error messages will be displayed for invalid operations.

Example Spreadsheet

The application interface looks like this:
<img width="899" alt="‏‏לכידה" src="https://github.com/user-attachments/assets/87286bac-5b47-49ba-8d7a-e45f73d1b80e" />





File Format

The application supports saving and loading spreadsheets using a CSV-like format:

X,Y,VALUE
0,0,1.0
1,0,=A0+1
2,0,=B0+1

Each row represents a cell with its X-coordinate, Y-coordinate, and stored value.

Formulas start with =.



This project is developed for educational purposes and is free to use and modify.
