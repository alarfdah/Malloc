# Assignment Information
### Author: Ahmed Radwan
### Date: 04/14/2019
### Class: CS5541
### Assignment: Assignment 4 - Malloc
### Email: ahmedabdelwaha.radwan@wmich.edu
 
# References (Can also be found above the function)
* Rounding a number to nearest multiple
    * https://stackoverflow.com/questions/3407012/c-rounding-up-to-the-nearest-multiple-of-a-number
    * Used in rounding the sizes to nearest multiple of 8 to ensure double alignment.
    * I didn't want to spend time thinking about math in the beginning :D
* How to write to a file in Java
    * https://www.baeldung.com/java-write-to-file
    * Used to refresh my memory on how to write to files in Java.

# What Language Have I Used?
* Java

# What IDE Have I Used?
* Eclipse

# How to Run?
* `Import...` the project into Eclipse
    * If `Import...` doesn't work, try opening the project using the `Open Projects from File System...`
* Place your input file in the root directory of the project or modify the current `input.txt` file
* Run the progam
* You will be asked to input the file name
    * Please type the file name with the extension (e.g.: `input.txt`)
* Then, you will be asked to choose the type of malloc: Implicit or Explicit
    * Implicit: 1
    * Explicit: 2
* Then, you will be asked to choose the type of search: First or Best fit
    * First: 1
    * Best: 2

# Design & Implementation
## Implicit
* The heap in my program is just an `int` array of words of size `1000`
    * The heap stores the first header at `1` for double alignment, skipping index zero `0`
    * The heap stores the first footer at `998` for double alignment, instead of index `999`
* The heap has the following structure
    * |`header`|`payload`|...|`payload`|`footer`|
* The payload is initialized with all `1`s in the beginning for reasons mentiond in the `Explicit` section

## Explicit
* Mostly the same as Implicit
* The first index `0` is used to store the `root` of the free list
* The next free block has the following structure
    * |`header`|`prev`|`next`|`payload`|...|`payload`|`footer`|
    * The prev, in this case, points to `0`, which is the index of `root` 
        * This is the reason why the payload is initialized with `1`s instead of `0`s
* A `prev` pointer always points to another free block's `prev` pointer
    * Except if it is the first free block, then it points to `root`
* A `next` pointer always points to another free block's `next` pointer
    * Except if it is the last, then its value is `-1`, as it doesn't point to anything
* The LIFO policy was used to free blocks
* When mallocing, if the last free block in the heap is allocated, sbrk is called
    * This is to ensure that `root` is always pointing to a free block
