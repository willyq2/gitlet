``# Gitlet Design Document

**William Webster**:

## Classes and Data Structures
**main**
- init: Initialize a Gitlet repository within current working directory (.gitlet hidden directory). Need a staging area (addition and removal) and commits tree (call commit function).

**commit** (object?)

`Linked list ` Pointer MASTER tracks initial commit and pointer HEAD tracks most recent commit. This will be the commits tree and it should allow us to restore old commits and add new commits.

`SHA-1` of parent commit.

`message`

`timestamp`

`trackedFiles`

- When this linked list is initialized we should have an initial commit that is its "head". This head node will have message = "initial commit", timestamp, and files = null.
- POINTERS: Upon initialization master branch is created and head pointer is at initial commit.

*tracked files: mappings from name of file to contents of file. 

Calling commit method on staged files:
1. Clone current head commit. 
2. Change metadata of new commit in to match staged file info. 
3. Have new commit track staged files and corresponding blobs. 
4. Change master and head pointers to match new commit.

**merge**

**add** 

- Adds current set of files to staging area. Takes a "snapshot" of contents of added file. This will involve writing and reading serialization.  

**remove**

**something to read and write blobs**

- Blob: The content of a file in a particular moment in time (object). This is what matters when a file is staged for addition. 

**status**
- Report status of file. Staged? Committed? Is the head pointer in sync with the master pointer?

**log**

**checkout**

## Algorithms
Determining merge conflicts?
- I'm not sure what this is quite yet, but I understand that we are merging two branches at the "latest common split point".
- We must check if a merge is necessary. 
- We must check if files branching from the split point have been modified.
- ...

Reading and writing blobs
- Use the util class to write linked commit list into a readable java file. 
- This will allow for persistence. 

init
- Initialize all directories.

Commit needs to have a function in main that adds to the log file in the .gitlet directory. 

## Persistence

- The main function that will have to deal with serialization is "add". Add must serialize the file into a blob and serialize the commit object that references that blob. This file will be read when getting the file. When does this occur?

- Basic folder will obviously be the hidden .gitlet directory. 
  - Within this we will have a staging directory.
    - Adding file 
    - Removing file
  - Commit directory 
    - This will contain the linked list that is our commit history. 
    - This commit history will be a linked list containg files that contain commit objects?
    - **commit directory contains a linked list of commit objects with head and master pointer**

**log will be an important command to look at in relation to persistence**
- Need to create a file that is continuously added to once a commit is made. This will probably be a file within the .gitlet directory. Do not forget to clear in between projects. ``

*I need to figure out how branches work because this could effect my data structures seriously. 