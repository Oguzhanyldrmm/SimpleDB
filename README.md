# SimpleDB

This repository contains the starter SimpleDB code used for CENG 435 assignments.

## Structure

- `SimpleDBEngine/`: database engine source code
- `SimpleDBClients/`: embedded and network client programs

Both projects include Eclipse project metadata so they can be imported directly as Java projects.

## Assignment 0 Status

Assignment 0 setup is complete:

- Java/JDK environment configured
- starter code imported
- Eclipse project files added
- embedded and network execution paths verified

## Import Into Eclipse

1. Open Eclipse.
2. Go to `File > Import > Existing Projects into Workspace`.
3. Select the repository root.
4. Import both `SimpleDBEngine` and `SimpleDBClients`.

## Notes

- `SimpleDBClients` depends on `SimpleDBEngine`.
- Runtime database folders and compiled outputs are ignored by Git.
- Future assignments should be committed incrementally on top of this repository.
